package com.github.yhnysc.replicavt.configsource.repo;

import com.github.yhnysc.replicavt.configsource.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;
import com.github.yhnysc.replicavt.configsource.api.RvtStruct;
import com.github.yhnysc.replicavt.configsource.exception.DatabaseException;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.github.yhnysc.replicavt.configsource.tran.OpAdapter;
import com.github.yhnysc.replicavt.configsource.tran.TxnAdapter;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class RvtEtcdBaseRepository<T extends RvtStruct, K extends RvtEtcdKey> implements RvtDataRepository<T, K>{
    protected final Client _etcdCli;
    protected final Gson _gson;
    protected final Class<T> _structClass;
    protected final RvtEtcdTransactionManager _etcdTransactionManager;

    public RvtEtcdBaseRepository(Client etcdCli, Gson gson, RvtEtcdTransactionManager etcdTransactionManager, Class<T> structClass){
        _etcdCli = etcdCli;
        _gson = gson;
        _structClass = structClass;
        _etcdTransactionManager = etcdTransactionManager;
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
        final TxnAdapter transaction = _etcdTransactionManager.getTransaction();
        final String fullKeyPath = etcdPrefix() + data.uniqueKey();
        try {
            transaction.Then(OpAdapter.put(
                    stringToEtcdByteSeq(fullKeyPath),
                    stringToEtcdByteSeq(_gson.toJson(data)),
                    PutOption.DEFAULT)
            );
            log.info("Save data, path [%s]".formatted(fullKeyPath));
        } catch (Exception e) {
            throw new DatabaseException("Failed to save %s".formatted(fullKeyPath), e);
        }
    }

    @Override
    public void delete(final K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final TxnAdapter transaction = _etcdTransactionManager.getTransaction();
        final String fullKeyPath = etcdPrefix() + key.etcdKey();
        try {
            transaction.Then(OpAdapter.delete(
                    stringToEtcdByteSeq(fullKeyPath),
                    DeleteOption.DEFAULT)
            );
            log.info("Delete data, path [%s]".formatted(fullKeyPath));
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete %s".formatted(fullKeyPath), e);
        }
    }

    @Override
    public Optional<T> findOne(final K key) {
        return findOne(GetOption.DEFAULT, key);
    }

    @Override
    public List<T> findAll(final K key){
        return findAll(GetOption.newBuilder().isPrefix(true).build(), key);
    }


    protected List<T> findAll(final GetOption getOption, final K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        if(!getOption.isPrefix()){
            throw new IllegalArgumentException("The GetOption must set to isPrefix(true) for this method");
        }
        final String prefixKeyPath = etcdPrefix() + key.etcdKey();
        try{
            // Search the data that have put operation in transaction
            final List<Map.Entry<ByteSequence,T>> dataListInTransaction = findDataInTransaction(getOption, key);

            final GetResponse resp = _etcdCli.getKVClient().get(
                    stringToEtcdByteSeq(prefixKeyPath), getOption).get();

            if(resp.getKvs().isEmpty()){
                // Simply return the data in transaction if any
                return dataListInTransaction.stream().map(Map.Entry::getValue).collect(Collectors.toList());
            }
            log.info("Retrieved all of '%s'".formatted(prefixKeyPath));
            final Map<ByteSequence, T> respMap = resp.getKvs().stream()
                    .map(kv -> Map.entry(kv.getKey(), _gson.fromJson(kv.getValue().toString(), _structClass)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // If resp and transaction have the same data, use data in transaction
            dataListInTransaction.forEach(
                    (tranKv) -> respMap.merge(tranKv.getKey(), tranKv.getValue(), (respData, tranData) -> tranData)
            );

            return respMap.values().stream().toList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve all of '%s'".formatted(prefixKeyPath));
        }
    }

    protected Optional<T> findOne(final GetOption getOption, final K key){
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final String fullKeyPath = etcdPrefix() + key.etcdKey();
        try{
            // Search the data that have put operation in transaction. If existed, return that value as it is the latest one
            final Optional<Map.Entry<ByteSequence,T>> dataInTransaction = findDataInTransaction(getOption, key).stream().findFirst();
            if(dataInTransaction.isPresent()){
                return Optional.of(dataInTransaction.get().getValue());
            }
            // Search from etcd cluster
            final GetResponse resp = _etcdCli.getKVClient().get(
                    stringToEtcdByteSeq(fullKeyPath), getOption).get();
            if(resp.getKvs().isEmpty()){
                return Optional.empty();
            }
            log.info("Retrieved %s".formatted(fullKeyPath));
            return Optional.of(_gson.fromJson(resp.getKvs().get(0).getValue().toString(), _structClass));
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve %s".formatted(fullKeyPath));
        }
    }

    protected List<Map.Entry<ByteSequence,T>> findDataInTransaction(final GetOption getOption, final K key){
        final List<Map.Entry<ByteSequence,T>> dataList = new ArrayList<>();
        final TxnAdapter transaction = _etcdTransactionManager.getTransaction();
        final String fullKeyPath = etcdPrefix() + key.etcdKey();

        final List<OpAdapter> operationsOnKey = transaction.getOperationsOnKey(stringToEtcdByteSeq(fullKeyPath), getOption.isPrefix());

        if(operationsOnKey.isEmpty()){
            // Not data found in transaction
            return dataList;
        }

        Collections.reverse(operationsOnKey);
        if(getOption.isPrefix()){
            // findAll with prefix
            // The logic like findOne but the operations involved multiple keys. Need to stored in Set for different key
            final Set<ByteSequence> processedKeyInTxnMap = new HashSet<>();
            for(final OpAdapter opAdapter : operationsOnKey){
                switch(opAdapter.getOpType()) {
                    case DELETE_RANGE:
                        // Deleted, simply mark as processed
                        processedKeyInTxnMap.add(opAdapter.getKey());
                        break;
                    case PUT:
                        if (!processedKeyInTxnMap.contains(opAdapter.getKey())) {
                            // Not processed and no deletion, add to the return data list and mark processed
                            final T data = _gson.fromJson(opAdapter.getValue().toString(), _structClass);
                            dataList.add(Map.entry(opAdapter.getKey(), data));
                            processedKeyInTxnMap.add(opAdapter.getKey());
                        }
                        break;
                    default:
                }
            }
        }else{
            // findOne
            // If the last operation is deletion or no update operation-> Not Found
            // else return the value of last update operation
            operationsOnKey.stream()
                    .filter(opAdapter -> {return opAdapter.isPut() || opAdapter.isDelete();})
                    .findFirst().ifPresent(lastOp ->{
                        if(lastOp.isPut()){
                            final T data = _gson.fromJson(lastOp.getValue().toString(), _structClass);
                            dataList.add(Map.entry(lastOp.getKey(), data));
                        }
                    });
        }
        return dataList;
    }

    private String etcdPrefix(){
        return _structClass.getAnnotation(EtcdPrefix.class).value() + ByteSequence.NAMESPACE_DELIMITER.toString();
    }

    private static ByteSequence stringToEtcdByteSeq(String s1){
        return ByteSequence.from(ByteString.copyFromUtf8(s1));
    }
}
