package com.github.yhnysc.replicavt.datasource.api;

import com.github.yhnysc.replicavt.datasource.entity.RvtChangeEventEntity;

import java.util.List;

public interface RvtChangeEventTableDao {
    void createChangeEventTable(String eventGroup);
    void createTrigger(String tableSchema, String tableName, String operation, String statement, int maxNumRecPerBat);
    List<RvtChangeEventEntity> findOutstandingChgEvents(String eventGroup, String tableGroup, int lastEventSeqNo);
    int deleteChgEvents(String eventGroup, String tableGroup, int deleteBeforeEventSeqNo);
}
