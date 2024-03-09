package com.github.yhnysc.replicavt.repo;

import com.github.yhnysc.replicavt.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.api.RvtEtcdKey;
import com.github.yhnysc.replicavt.api.RvtStruct;
import com.github.yhnysc.replicavt.exception.DatabaseException;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class RvtEtcdBaseRepository<T extends RvtStruct, K extends RvtEtcdKey> implements RvtDataRepository<T, K>{
    protected final Client _etcdCli;
    protected final Gson _gson;
    protected final Class<T> _structClass;

    public RvtEtcdBaseRepository(Client etcdCli, Gson gson, Class<T> structClass){
        _etcdCli = etcdCli;
        _gson = gson;
        _structClass = structClass;
        // Check the class needed to be annotated with EtcdPrefix.class
        if(!_structClass.isAnnotationPresent(EtcdPrefix.class)){
            throw new IllegalArgumentException("The class %s is not annootated with EtcdPrefix".formatted(_structClass.getSimpleName()));
        }
    }

    @Override
    public void save(final T data) {
        if (data == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final String fullKeyPath = etcdPrefix() + data.uniqueKey();
        try {
            _etcdCli.getKVClient().put(
                    ByteSequence.from(ByteString.copyFromUtf8(fullKeyPath)),
                    ByteSequence.from(ByteString.copyFromUtf8(_gson.toJson(data)))
            ).get(30, TimeUnit.SECONDS);
            log.info("Saved data, path [%s]".formatted(fullKeyPath));
        } catch (Exception e) {
            throw new DatabaseException("Failed to save %s".formatted(fullKeyPath), e);
        }
    }

    @Override
    public void delete(final K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final String fullKeyPath = etcdPrefix() + key.etcdKey();
        try {
            DeleteResponse resp = _etcdCli.getKVClient().delete(
                    ByteSequence.from(ByteString.copyFromUtf8(fullKeyPath))
            ).get();

            if (resp.getDeleted() == 1) {
                log.info("Deleted data, path [%s]".formatted(fullKeyPath));
            } else {
                throw new DatabaseException("Failed to delete %s".formatted(fullKeyPath));
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete %s".formatted(fullKeyPath), e);
        }
    }

    @Override
    public Optional<T> findOne(final K key) {
        return findOne(Optional.empty(), key);
    }

    @Override
    public List<T> findAll(final K key){
        return findAll(Optional.of(GetOption.newBuilder().isPrefix(true).build()), key);
    }


    protected List<T> findAll(final Optional<GetOption> getOption, final K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        if(getOption.isPresent() && !getOption.get().isPrefix()){
            throw new IllegalArgumentException("The GetOption must set to isPrefix(true) for this method");
        }
        final String prefixKeyPath = etcdPrefix() + key.etcdKey();
        try{
            final List<T> resultList = new ArrayList<>();
            final GetResponse resp;
            if(getOption.isPresent()) {
                resp = _etcdCli.getKVClient().get(
                        ByteSequence.from(ByteString.copyFromUtf8(prefixKeyPath)), getOption.get()).get();
            }else{
                resp = _etcdCli.getKVClient().get(
                        ByteSequence.from(ByteString.copyFromUtf8(prefixKeyPath))).get();
            }
            if(resp.getKvs().isEmpty()){
                log.info("Failed to retrieve all of '%s'".formatted(prefixKeyPath));
                return null;
            }
            log.info("Retrieved all of '%s'".formatted(prefixKeyPath));
            resp.getKvs().stream().map(KeyValue::getValue).forEach(
                    jsonByteSeq-> resultList.add(_gson.fromJson(jsonByteSeq.toString(), _structClass))
            );
            return resultList;
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve all of '%s'".formatted(prefixKeyPath));
        }
    }

    protected Optional<T> findOne(final Optional<GetOption> getOption, final K key){
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final String fullKeyPath = etcdPrefix() + key.etcdKey();
        try{
            final GetResponse resp;
            if(getOption.isPresent()) {
                resp = _etcdCli.getKVClient().get(
                        ByteSequence.from(ByteString.copyFromUtf8(fullKeyPath)), getOption.get()).get();
            }else{
                resp = _etcdCli.getKVClient().get(
                        ByteSequence.from(ByteString.copyFromUtf8(fullKeyPath))).get();
            }
            if(resp.getKvs().isEmpty()){
                log.info("Failed to retrieve %s".formatted(fullKeyPath));
                return Optional.empty();
            }
            log.info("Retrieved %s".formatted(fullKeyPath));
            return Optional.of(_gson.fromJson(resp.getKvs().get(0).getValue().toString(), _structClass));
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve %s".formatted(fullKeyPath));
        }
    }

    private String etcdPrefix(){
        return _structClass.getAnnotation(EtcdPrefix.class).value() + ByteSequence.NAMESPACE_DELIMITER.toString();
    }
}
