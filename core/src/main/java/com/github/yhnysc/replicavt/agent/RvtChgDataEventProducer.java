package com.github.yhnysc.replicavt.agent;

import com.github.yhnysc.replicavt.configsource.api.RvtTablesRepository;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
