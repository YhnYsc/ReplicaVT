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
        if (!getOption.isPrefix()){
            throw new IllegalArgumentException("The GetOption must set to isPrefix(true) for this method");
        }
        final TxnAdapter transaction = _etcdTransactionManager.getTransaction();
        final String prefixKeyPath = etcdPrefix() + key.etcdKey();
        try{
            final Map<ByteSequence, OpAdapter> lastOperationOnKeys = transaction.getLastOperationOnKeys(stringToEtcdByteSeq(prefixKeyPath));

            final GetResponse resp = _etcdCli.getKVClient().get(
                    stringToEtcdByteSeq(prefixKeyPath), getOption).get();

            if(resp.getKvs().isEmpty()){
                // Simply return the data in transaction if any
                final List<T> transactionOnlyResult = filterAndSortDataInTransaction(lastOperationOnKeys.values().stream().toList(), getOption).stream()
                        .map(sortedOperationInTransaction -> convertJsonToObj(sortedOperationInTransaction.getValue()))
                        .collect(Collectors.toList());
                log.info("Retrieved prefix %s, count[%d] in transaction".formatted(prefixKeyPath, transactionOnlyResult.size()));
                return transactionOnlyResult;
            }
            log.info("Retrieved all of '%s'".formatted(prefixKeyPath));
            final List<OpAdapter> resultList = new ArrayList<>();
            resp.getKvs().forEach(kvFromResp -> {
                final OpAdapter lastOperationInTransaction = lastOperationOnKeys.remove(kvFromResp.getKey());
                if (lastOperationInTransaction != null && lastOperationInTransaction.isPut()){
                    // Use the value in the update operation as it is the latest
                    resultList.add(lastOperationInTransaction);
                }else {
                    // Simply use the value from the response
                    resultList.add(OpAdapter.builder()
                            .key(kvFromResp.getKey())
                            .value(kvFromResp.getValue())
                            .build());
                }
            });
            //TODO: 1. Sort the operationsInTransaction by GetOption.SortTarget and SortOrder
            //      2. merge the sorted list to response list (because the response list already sorted by EtcdCli
            //      - Sorting by CREATE, MOD, VERSION will always append to first (Desc) / last (Asc) of the response list
            //      - Sorting by KEY, VALUE will compare value from two lists
            return null;
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve all of '%s'".formatted(prefixKeyPath));
        }
    }

    protected Optional<T> findOne(final GetOption getOption, final K key){
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null!");
        }
        final TxnAdapter transaction = _etcdTransactionManager.getTransaction();
        final String fullKeyPath = etcdPrefix() + key.etcdKey();
        try{
            final Optional<OpAdapter> lastOperationOnKey = transaction.getLastOperationOnKey(stringToEtcdByteSeq(fullKeyPath));
            if (lastOperationOnKey.isPresent()){
                // If the last operation is update, return value of the update operation
                // If the last operation is deletion, return no data
                log.info("Retrieved %s in transaction".formatted(fullKeyPath));
                return Optional.ofNullable(lastOperationOnKey.get().isPut() ? convertJsonToObj(lastOperationOnKey.get().getValue()) : null);
            }else {
                // Search from etcd cluster
                final GetResponse resp = _etcdCli.getKVClient().get(
                        stringToEtcdByteSeq(fullKeyPath), getOption).get();
                if (resp.getKvs().isEmpty()) {
                    return Optional.empty();
                }
                log.info("Retrieved %s".formatted(fullKeyPath));
                return Optional.of(_gson.fromJson(resp.getKvs().get(0).getValue().toString(), _structClass));
            }
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
                            final T data = convertJsonToObj(opAdapter.getValue());
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
                    .filter(opAdapter -> opAdapter.isPut() || opAdapter.isDelete())
                    .findFirst().ifPresent(lastOp ->{
                        if(lastOp.isPut()){
                            final T data = convertJsonToObj(lastOp.getValue());
                            dataList.add(Map.entry(lastOp.getKey(), data));
                        }
                    });
        }
        return dataList;
    }

    private T convertJsonToObj(ByteSequence value){
        return _gson.fromJson(value.toString(), _structClass);
    }

    private String etcdPrefix(){
        return _structClass.getAnnotation(EtcdPrefix.class).value() + ByteSequence.NAMESPACE_DELIMITER.toString();
    }

    private static ByteSequence stringToEtcdByteSeq(String s1){
        return ByteSequence.from(ByteString.copyFromUtf8(s1));
    }

    private static List<OpAdapter> filterAndSortDataInTransaction(List<OpAdapter> remainingOperationFromMap, GetOption getOption){
        return remainingOperationFromMap.stream()
                .filter(OpAdapter::isPut) // ignore data with last operation is deletion
                .sorted((op1, op2) -> {
                    switch(getOption.getSortField()){
                        case KEY:
                            return GetOption.SortOrder.ASCEND == getOption.getSortOrder() ?
                                    op1.getKey().toString().compareTo(op2.getKey().toString()) :
                                    op2.getKey().toString().compareTo(op1.getKey().toString());
                        case VALUE:
                            return GetOption.SortOrder.ASCEND == getOption.getSortOrder() ?
                                    op1.getValue().toString().compareTo(op2.getValue().toString()) :
                                    op2.getValue().toString().compareTo(op1.getValue().toString());
                        case CREATE:
                        case MOD:
                        case VERSION:
                        default:
                            //Create, Mod, Version just order by timestamp
                            return GetOption.SortOrder.ASCEND == getOption.getSortOrder() ?
                                    op1.getOpTime().compareTo(op2.getOpTime()) :
                                    op2.getOpTime().compareTo(op1.getOpTime());
                    }
                })
                .collect(Collectors.toList());
    }
}
