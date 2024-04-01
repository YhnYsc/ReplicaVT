package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.configsource.api.RvtStruct;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@EtcdPrefix("eventgroups")
public class RvtEventGroups implements RvtStruct, Serializable {

    private String _eventGroupName;
    private int _bulkSize;
    private List<RvtTableGroups> _tableGroups;
    private OffsetDateTime _updateTime;

    @Override
    public String uniqueKey() {
        // eventgroups/sampleTableGroup
        return _eventGroupName;
    }

    public void addTableGroup(RvtTableGroups tableGroup){
        if(_tableGroups == null){
            _tableGroups = new ArrayList<>();
        }
        _tableGroups.add(tableGroup);
    }

    public String getEventGroupName() {
        return _eventGroupName;
    }

    public void setEventGroupName(String eventGroupName) {
        _eventGroupName = eventGroupName;
    }

    public List<RvtTableGroups> getTableGroups() {
        return _tableGroups;
    }

    public void setTableGroups(List<RvtTableGroups> tableGroup) {
        _tableGroups = tableGroup;
    }

    public int getBulkSize() {
        return _bulkSize;
    }

    public void setBulkSize(int bulkSize) {
        _bulkSize = bulkSize;
    }

    @Override
    public OffsetDateTime getTimestamp() {
        return _updateTime;
    }

    @Override
    public void setTimestamp(OffsetDateTime updateTime) {
        _updateTime = updateTime;
    }

}
