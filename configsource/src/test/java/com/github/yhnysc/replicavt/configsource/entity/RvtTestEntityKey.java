package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;

public class RvtTestEntityKey implements RvtEtcdKey {
    private String _key1;
    private String _key2;

    @Override
    public String etcdKey() {
        return RvtEtcdKey.contructEtcdKey(_key1, _key2);
    }

    public RvtTestEntityKey(String key1, String key2) {
        _key1 = key1;
        _key2 = key2;
    }
}
