package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;

public class RvtNodeConfigKey implements RvtEtcdKey {
    private String _nodeName;

    @Override
    public String etcdKey() {
        return _nodeName;
    }

    public RvtNodeConfigKey(String nodeName) {
        _nodeName = nodeName;
    }
}
