package com.github.yhnysc.replicavt.datasource.entity;

import java.sql.Timestamp;

public class RvtChangeEventEntity{
    private String _tableGroup;
    private String _tableName;
    private String _tableVersion;
    private Long _seqNo;
    private String _operation;
    private String _mode;
    private String _payloadKeyOld;
    private String _payloadKeyNew;
    private String _payload;
    private String _payloadCmpOld;
    private String _payloadCmpNew;
    private String _eventFrom;
    private java.sql.Timestamp _createTs;
    private java.sql.Timestamp _updateTs;

    public String getTableGroup() {
        return _tableGroup;
    }

    public void setTableGroup(String tableGroup) {
        _tableGroup = tableGroup;
    }

    public Long getSeqNo() {
        return _seqNo;
    }

    public void setSeqNo(Long seqNo) {
        _seqNo = seqNo;
    }

    public String getTableName() {
        return _tableName;
    }

    public void setTableName(String tableName) {
        _tableName = tableName;
    }

    public String getTableVersion() {
        return _tableVersion;
    }

    public void setTableVersion(String tableVersion) {
        _tableVersion = tableVersion;
    }

    public String getOperation() {
        return _operation;
    }

    public void setOperation(String operation) {
        _operation = operation;
    }

    public String getMode() {
        return _mode;
    }

    public void setMode(String mode) {
        _mode = mode;
    }

    public String getPayloadKeyOld() {
        return _payloadKeyOld;
    }

    public void setPayloadKeyOld(String payloadKeyOld) {
        _payloadKeyOld = payloadKeyOld;
    }

    public String getPayloadKeyNew() {
        return _payloadKeyNew;
    }

    public void setPayloadKeyNew(String payloadKeyNew) {
        _payloadKeyNew = payloadKeyNew;
    }

    public String getPayload() {
        return _payload;
    }

    public void setPayload(String payload) {
        _payload = payload;
    }

    public String getPayloadCmpOld() {
        return _payloadCmpOld;
    }

    public void setPayloadCmpOld(String payloadCmpOld) {
        _payloadCmpOld = payloadCmpOld;
    }

    public String getPayloadCmpNew() {
        return _payloadCmpNew;
    }

    public void setPayloadCmpNew(String payloadCmpNew) {
        _payloadCmpNew = payloadCmpNew;
    }

    public String getEventFrom() {
        return _eventFrom;
    }

    public void setEventFrom(String eventFrom) {
        _eventFrom = eventFrom;
    }

    public Timestamp getCreateTs() {
        return _createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        _createTs = createTs;
    }

    public Timestamp getUpdateTs() {
        return _updateTs;
    }

    public void setUpdateTs(Timestamp updateTs) {
        _updateTs = updateTs;
    }
}
