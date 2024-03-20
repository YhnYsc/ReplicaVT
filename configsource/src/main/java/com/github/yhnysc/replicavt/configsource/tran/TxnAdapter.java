package com.github.yhnysc.replicavt.configsource.tran;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Txn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public void commit(){
        try {
            // Convert the OpAdapter to Op (the Etcd transaction's operation object)
            _operationsInTransaction.stream()
                    .map(opAdapter -> opAdapter.toOp())
                    .forEach(op -> {
                                _transaction.Then(op);
                            }
                    );
            _transaction.commit();
        }finally{
            // Clear the transactions cache anyway
            _operationsInTransaction.clear();
        }
    }
}
