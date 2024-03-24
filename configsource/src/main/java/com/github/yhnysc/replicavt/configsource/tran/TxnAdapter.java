package com.github.yhnysc.replicavt.configsource.tran;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Txn;

import java.util.*;
import java.util.stream.Collectors;

public final class TxnAdapter{
    final Txn _transaction;
    final List<OpAdapter> _operationsInTransaction;

    public TxnAdapter(final Txn transaction){
        _transaction = transaction;
        _operationsInTransaction = new ArrayList<>();
    }

    public TxnAdapter Then(OpAdapter... ops){
        return Then(Arrays.asList(ops));
    }

    public TxnAdapter Then(List<OpAdapter> ops){
        _operationsInTransaction.addAll(ops);
        return this;
    }

    public List<OpAdapter> getOperationsOnKey(ByteSequence key, boolean isPrefix){
        return _operationsInTransaction.stream()
                .filter(Objects::nonNull)
                .filter(opAdapter -> opAdapter.getKey() != null && (
                    (isPrefix && opAdapter.getKey().startsWith(key)) ||
                    (!isPrefix && opAdapter.getKey().equals(key))))
                .map(opAdapter -> opAdapter.toBuilder().build())
                .collect(Collectors.toList());
    }

    public Optional<OpAdapter> getLastOperationOnKey(ByteSequence key){
        final List<OpAdapter> operationsOnKey = getOperationsOnKey(key, false);
        Collections.reverse(operationsOnKey);
        return operationsOnKey.stream().filter(opAdapter -> opAdapter.isPut() || opAdapter.isDelete()).findFirst();
    }

    public Map<ByteSequence, OpAdapter> getLastOperationOnKeys(ByteSequence prefixKey){
        final Map<ByteSequence, OpAdapter> lastOperationOnKeysMap = new HashMap<>();
        final List<OpAdapter> operationsOnKey = getOperationsOnKey(prefixKey, true);
        operationsOnKey.forEach(opAdapter -> {
            lastOperationOnKeysMap.put(opAdapter.getKey(), opAdapter);
        });
        return lastOperationOnKeysMap;
    }

    public void commit(){
        try {
            // Convert the OpAdapter to Op (the Etcd transaction's operation object)
            _operationsInTransaction.stream()
                    .map(opAdapter -> opAdapter.toOp())
                    .forEach(op -> {
                                _transaction.Then(op);
                            }
                    );
            _transaction.commit().join();
        } finally{
            // Clear the transactions cache anyway
            _operationsInTransaction.clear();
        }
    }

    public void rollback(){
        _operationsInTransaction.clear();
    }
}
