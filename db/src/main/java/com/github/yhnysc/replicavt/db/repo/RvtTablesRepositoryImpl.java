package com.github.yhnysc.replicavt.db.repo;

import com.github.yhnysc.replicavt.api.RvtTablesRepository;
import com.github.yhnysc.replicavt.db.RvtEtcdBaseRepository;
import com.github.yhnysc.replicavt.db.RvtEtcdKey;
import com.github.yhnysc.replicavt.db.data.RvtTables;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.GetOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RvtTablesRepositoryImpl extends RvtEtcdBaseRepository<RvtTables, RvtEtcdKey> implements RvtTablesRepository<RvtTables, RvtEtcdKey> {
    @Autowired
    public RvtTablesRepositoryImpl(final Client etcdCli, final Gson gson) {
        super(etcdCli, gson, RvtTables.class);
    }

    @Override
    public Optional<RvtTables> find(String tableSchema, String tableName, int tableVersion){
        return super.findOne(()-> RvtEtcdKey.contructEtcdKey(tableSchema, tableName, String.valueOf(tableVersion)));
    }

    @Override
    public Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName){
        GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.CREATE)
                .withSortOrder(GetOption.SortOrder.DESCEND)
                .withLimit(1)
                .build();
        final List<RvtTables> tablesList = super.findAll(Optional.of(getOption), () -> RvtEtcdKey.contructEtcdKey(tableSchema, tableName));
        return Optional.ofNullable(tablesList.size() > 0 ? tablesList.get(0) : null);
    }
}
