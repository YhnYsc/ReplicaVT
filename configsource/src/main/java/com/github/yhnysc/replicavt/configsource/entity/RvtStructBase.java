package com.github.yhnysc.replicavt.configsource.entity;

import com.github.yhnysc.replicavt.configsource.api.RvtStruct;

import java.io.Serializable;
import java.time.OffsetDateTime;

public abstract class RvtStructBase implements RvtStruct, Serializable {
    protected OffsetDateTime _timestamp;

    @Override
    public OffsetDateTime getTimestamp() {
        return _timestamp;
    }

    @Override
    public void setTimestamp(OffsetDateTime timestamp) {
        _timestamp = timestamp;
    }

}
