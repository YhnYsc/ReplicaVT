package com.github.yhnysc.replicavt.configsource.svc;

import com.github.yhnysc.replicavt.configsource.tran.TxnAdapter;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RvtEtcdTransactionManager {
    protected final Client _etcdCli;
    protected final ThreadLocal<TxnAdapter> _transaction = new ThreadLocal<>();

    @Autowired
    public RvtEtcdTransactionManager(Client etcdCli) {
        _etcdCli = etcdCli;
    }

    public TxnAdapter getTransaction(){
        if (_transaction.get() == null){
            _transaction.set(new TxnAdapter(_etcdCli.getKVClient().txn()));
        }
        return  _transaction.get();
    }

//    public void lockAcquire(String key, long leaseTimeInSecond){
//        try {
//            long leaseID = _etcdCli.getLeaseClient().grant(leaseTimeInSecond).get().getID();
//            _etcdCli.getLockClient().lock(ByteSequence.from(ByteString.copyFromUtf8(key)), leaseID);
//        } catch (InterruptedException | ExecutionException e) {
//            throw new DatabaseException("Failed to create lease", e);
//        }
//    }
//
//    public void lockRelinquish(String key){
//        _etcdCli.getLockClient().unlock(ByteSequence.from(ByteString.copyFromUtf8(key)));
//    }ß
}
