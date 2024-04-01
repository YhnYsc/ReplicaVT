package com.github.yhnysc.replicavt.configsource.repo;

import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;
import com.github.yhnysc.replicavt.configsource.api.RvtTablesRepository;
import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.configsource.entity.RvtTablesKey;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.GetOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RvtTablesRepositoryImpl extends RvtEtcdBaseRepository<RvtTables, RvtTablesKey> implements RvtTablesRepository {
    @Autowired
    public RvtTablesRepositoryImpl(final Client etcdCli, final Gson gson, final RvtEtcdTransactionManager etcdTransactionManager) {
        super(etcdCli, gson, etcdTransactionManager, RvtTables.class);
    }

    @Override
    public Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName){
        GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.CREATE)
                .withSortOrder(GetOption.SortOrder.DESCEND)
                .withLimit(1)
                .build();
        // Override the etcdKey() to include only tableSchema and tableName (without tableVersion), as a prefix
        final List<RvtTables> tablesList = super.findAll(getOption, () -> RvtEtcdKey.contructEtcdKey(tableSchema, tableName));
        return Optional.ofNullable(tablesList.size() > 0 ? tablesList.get(0) : null);
    }
}
