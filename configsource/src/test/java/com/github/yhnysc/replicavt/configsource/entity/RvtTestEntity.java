package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;

@EtcdPrefix("test")
public class RvtTestEntity extends RvtStructBase {

    private String _key1;
    private String _key2;
    private int _value1;
    private String _value2;

    @Override
    public String uniqueKey() {
        return RvtEtcdKey.contructEtcdKey(_key1, _key2);
    }

    public String getKey1() {
        return _key1;
    }

    public void setKey1(String key1) {
        _key1 = key1;
    }

    public String getKey2() {
        return _key2;
    }

    public void setKey2(String key2) {
        _key2 = key2;
    }

    public int getValue1() {
        return _value1;
    }

    public void setValue1(int value1) {
        _value1 = value1;
    }

    public String getValue2() {
        return _value2;
    }

    public void setValue2(String value2) {
        _value2 = value2;
    }

}
