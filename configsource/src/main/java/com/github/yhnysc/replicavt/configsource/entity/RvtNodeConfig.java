package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.annotation.EtcdPrefix;

@EtcdPrefix("NodeConfig")
public class RvtNodeConfig extends RvtStructBase{
    private String _nodeName;
    private String _dbType;
    private String _dbHost;
    private String _dbPort;
    private String _dbName;
    private String _dbSchema;
    private String _dbUser;
    private String _dbPassword;
    private boolean _dbSslEnabled;

    @Override
    public String uniqueKey() {
        return _nodeName;
    }

    public String getNodeName() {
        return _nodeName;
    }

    public void setNodeName(String nodeName) {
        _nodeName = nodeName;
    }

    public String getDbType() {
        return _dbType;
    }

    public void setDbType(String dbType) {
        _dbType = dbType;
    }

    public String getDbHost() {
        return _dbHost;
    }

    public void setDbHost(String dbHost) {
        _dbHost = dbHost;
    }

    public String getDbPort() {
        return _dbPort;
    }

    public void setDbPort(String dbPort) {
        _dbPort = dbPort;
    }

    public String getDbName() {
        return _dbName;
    }

    public void setDbName(String dbName) {
        _dbName = dbName;
    }

    public String getDbSchema() {
        return _dbSchema;
    }

    public void setDbSchema(String dbSchema) {
        _dbSchema = dbSchema;
    }

    public String getDbUser() {
        return _dbUser;
    }

    public void setDbUser(String dbUser) {
        _dbUser = dbUser;
    }

    public String getDbPassword() {
        return _dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        _dbPassword = dbPassword;
    }

    public boolean isDbSslEnabled() {
        return _dbSslEnabled;
    }

    public void setDbSslEnabled(boolean dbSslEnabled) {
        _dbSslEnabled = dbSslEnabled;
    }
}
