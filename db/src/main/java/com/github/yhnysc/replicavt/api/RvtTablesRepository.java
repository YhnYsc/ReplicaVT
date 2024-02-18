package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.db.RvtDataRepository;
import com.github.yhnysc.replicavt.db.data.RvtTables;
import com.github.yhnysc.replicavt.db.data.RvtTablesKey;

import java.util.Optional;

public interface RvtTablesRepository extends RvtDataRepository<RvtTables, RvtTablesKey> {
    Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName);
}
