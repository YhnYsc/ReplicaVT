package com.github.yhnysc.replicavt.configsource.repo;

import com.github.yhnysc.replicavt.configsource.entity.RvtNodeConfig;
import com.github.yhnysc.replicavt.configsource.entity.RvtNodeConfigKey;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RvtNodeConfigRepository extends RvtEtcdBaseRepository<RvtNodeConfig, RvtNodeConfigKey>{
    @Autowired
    public RvtNodeConfigRepository(Client etcdCli, Gson gson, RvtEtcdTransactionManager etcdTransactionManager) {
        super(etcdCli, gson, etcdTransactionManager, RvtNodeConfig.class);
    }
}
