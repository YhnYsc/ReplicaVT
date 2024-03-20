package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;

public class RvtTablesKey implements RvtEtcdKey {

    private String _tableSchema;
    private String _tableName;
    private Integer _tableVersion;

    @Override
    public String etcdKey() {
        return RvtEtcdKey.contructEtcdKey(_tableSchema, _tableName, String.valueOf(_tableVersion));
    }

    public RvtTablesKey(String tableSchema, String tableName) {
        _tableSchema = tableSchema;
        _tableName = tableName;
    }

    public RvtTablesKey(String tableSchema, String tableName, Integer tableVersion) {
        _tableSchema = tableSchema;
        _tableName = tableName;
        _tableVersion = tableVersion;
    }
}
