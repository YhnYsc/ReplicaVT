package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.entity.RvtTables;
import com.github.yhnysc.replicavt.entity.RvtTablesKey;
import com.github.yhnysc.replicavt.repo.RvtDataRepository;

import java.util.Optional;

public interface RvtTablesRepository extends RvtDataRepository<RvtTables, RvtTablesKey> {
    Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName);
}
