package com.github.yhnysc.replicavt.sql;

import com.github.yhnysc.replicavt.annotation.MySqlBean;
import com.github.yhnysc.replicavt.datasource.api.RvtChangeEventTableDao;
import com.github.yhnysc.replicavt.datasource.entity.RvtChangeEventEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class RvtChangeEventTableMySqlDaoImpl implements RvtChangeEventTableDao {

    private final RvtChangeEventTableMySqlMapper _mapper;
    private final JdbcTemplate _jdbcTemplate;

    public RvtChangeEventTableMySqlDaoImpl(final RvtChangeEventTableMySqlMapper mapper, final JdbcTemplate jdbcTemplate) {
        _mapper = mapper;
        _jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createChangeEventTable(String eventGroup){
        _mapper.createChangeEventTable(eventGroup);
    }

    @Override
    public void createTrigger(String tableSchema, String tableName, String operation, String statement, int maxNumRecPerBat) {
        
    }

    @Override
    public List<RvtChangeEventEntity> findOutstandingChgEvents(String eventGroup, String tableGroup, int lastEventSeqNo) {
        return _mapper.findOutstandingChgEvents(eventGroup, tableGroup, lastEventSeqNo);
    }

    @Override
    public int deleteChgEvents(String eventGroup, String tableGroup, int deleteBeforeEventSeqNo) {
        return _mapper.deleteChgEvents(eventGroup, tableGroup, deleteBeforeEventSeqNo);
    }
}
