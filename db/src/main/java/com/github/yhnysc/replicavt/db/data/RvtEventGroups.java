package com.github.yhnysc.replicavt.db.data;

import com.github.yhnysc.replicavt.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.db.RvtStruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EtcdPrefix("eventgroups")
public class RvtEventGroups implements RvtStruct, Serializable {

    private String _eventGroupName;
    private int _bulkSize;
    private List<RvtTableGroups> _tableGroups;

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
}
