package com.github.yhnysc.replicavt.agent;

import com.github.yhnysc.replicavt.api.RvtTablesRepository;
import com.github.yhnysc.replicavt.db.data.RvtTables;
import com.google.gson.Gson;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Component
public class RvtChgDataEventProducer {
    final Client _etcdCli;

    final Gson _gson;

    final RvtTablesRepository _tablesRepo;

    @Autowired
    public RvtChgDataEventProducer(final Client etcdCli, final Gson gson, final RvtTablesRepository tablesRepo){
        _etcdCli = etcdCli;
        _gson = gson;
        _tablesRepo = tablesRepo;
    }

    public void test() throws ExecutionException, InterruptedException {
        GetResponse getResp = _etcdCli.getKVClient().get(
                ByteSequence.from("animal".getBytes())).get();

        final RvtTables table = _gson.fromJson(getResp.getKvs().get(0).getValue().toString(), RvtTables.class);
        table.setCreateTimestamp(Timestamp.from(Instant.now()));
        _tablesRepo.save(table);
    }
}
