package com.github.yhnysc.replicavt.datasource;

public enum DatabaseEngines {
    MYSQL("MYSQL", "mysql"),
    MSSQL("MSSQL", "sqlserver"),
    ORACLE("ORACLE", "oracle"),
    POSTGRESQL("POSTGRESQL", "postgresql"),
    DB2("DB2", "db2");

    private final String _textOfEngine;
    private final String _jdbcUrl;
    DatabaseEngines(String textOfEngine, String jdbcUrl){
        _textOfEngine = textOfEngine;
        _jdbcUrl = jdbcUrl;
    }

    public String getJdbcUrl(){
        return _jdbcUrl;
    }

    @Override
    public String toString(){
        return _textOfEngine;
    }
}
