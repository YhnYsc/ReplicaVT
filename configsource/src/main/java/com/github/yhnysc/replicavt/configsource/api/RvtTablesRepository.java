package com.github.yhnysc.replicavt.configsource.api;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.configsource.entity.RvtTablesKey;
import com.github.yhnysc.replicavt.configsource.repo.RvtDataRepository;

import java.util.Optional;

public interface RvtTablesRepository extends RvtDataRepository<RvtTables, RvtTablesKey> {
    Optional<RvtTables> findLatestTableVer(String tableSchema, String tableName);
}
