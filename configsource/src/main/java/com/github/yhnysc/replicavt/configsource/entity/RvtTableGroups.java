package com.github.yhnysc.replicavt.configsource.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RvtTableGroups implements Serializable {

    private String _tableGroupName;
    private short _priority;
    private int _batchSize;
    private List<String> _tables;

    public void addTable(String tableName){
        if(_tables == null){
            _tables = new ArrayList<>();
        }
        _tables.add(tableName);
    }

    public short getPriority() {
        return _priority;
    }

    public void setPriority(short priority) {
        _priority = priority;
    }

    public int getBatchSize() {
        return _batchSize;
    }

    public void setBatchSize(int batchSize) {
        _batchSize = batchSize;
    }

    public List<String> getTables() {
        return _tables;
    }

    public void setTables(List<String> tables) {
        _tables = tables;
    }

    public String getTableGroupName() {
        return _tableGroupName;
    }

    public void setTableGroupName(String tableGroupName) {
        _tableGroupName = tableGroupName;
    }
}
