package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.db.RvtDataRepository;
import com.github.yhnysc.replicavt.db.data.RvtTables;

import java.util.Optional;

public interface RvtTablesRepository<RvtTables, K> extends RvtDataRepository<RvtTables, K> {
    Optional<RvtTables> find(String tableSchema, String tableName, int tableVersion);
    Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName);
}
