package com.github.yhnysc.replicavt.configsource.entity;


import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;

public class RvtEventGroupsKey implements RvtEtcdKey {

    private String _eventGroupName;

    @Override
    public String etcdKey() {
        return _eventGroupName;
    }

    public RvtEventGroupsKey(String eventGroupName) {
        _eventGroupName = eventGroupName;
    }
}
