package com.github.yhnysc.replicavt.sql;

import com.github.yhnysc.replicavt.datasource.api.RvtChangeEventTableMapper;
import com.github.yhnysc.replicavt.datasource.entity.RvtChangeEventEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RvtChangeEventTableMySqlMapper extends RvtChangeEventTableMapper {

    final static String MYBATIS_INSERT_UPDATE_DELETE_CASES_TMPL = """
            ]]>
            <choose>
                <when test="operation == '%s'">
                <![CDATA[
                    %s
                ]]>
                </when>
                <when test="operation == '%s'">
                 <![CDATA[
                    %s
                ]]>
                </when>
                <when test="operation == '%s'">
                <![CDATA[
                    %s
                ]]>
                </when>
            </choose>
            <![CDATA[
            """;
    final static String SQL_TRIGGER_INSERT_EVENT = MYBATIS_INSERT_UPDATE_DELETE_CASES_TMPL.formatted(
            ""+
            "INSERT", """
                    
            """,
            "UPDATE", """
                    
            """,
            "DELETE", """
                    
            """
    );
    final static String SQL_TRIGGER_UPDATE_EVENT = MYBATIS_INSERT_UPDATE_DELETE_CASES_TMPL.formatted(
            ""+
            "INSERT", """
                
            """,
            "UPDATE", """
                    
            """,
            "DELETE", """
                    
            """
    );


    @Override
    @Update("""
            CREATE TABLE IF NOT EXISTS EventGroup_${eventGroup} ( 
            TableGroup     VARCHAR(255) NOT NULL DEFAULT '', 
            TableName      VARCHAR(255) NOT NULL DEFAULT '',
            TableVersion   Integer NOT NULL DEFAULT 1,
            SeqNo          BIGINT NOT NULL AUTO_INCREMENT,
            Operation      CHAR(1) NOT NULL DEFAULT '',
            Mode           CHAR(1) NOT NULL DEFAULT '',
            PayloadKeyOld  TEXT NULL,
            PayloadKeyNew  TEXT NULL,
            Payload        LONGTEXT NULL,
            PayloadCmpOld  TEXT NULL,
            PayloadCmpNew  TEXT NULL,
            EventFrom      VARCHAR(100) NOT NULL DEFAULT '',
            CreateTs       TIMESTAMP(6) NOT NULL DEFAULT NOW(6),
            UpdateTs       TIMESTAMP(6) NOT NULL DEFAULT NOW(6),
            INDEX(SeqNo)
            )
            """)
    void createChangeEventTable(@Param("eventGroup") String eventGroup);

    @Override
    void createTrigger(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName,
                       @Param("operation") String operation, @Param("statement") String statement,
                       @Param("maxNumRecPerBat") int maxNumRecPerBat);

    @Override
    @Select("SELECT * FROM EventGroup_${eventGroup} WHERE TableGroup = #{tableGroup} AND SeqNo > #{lastEventSeqNo}")
    List<RvtChangeEventEntity> findOutstandingChgEvents(@Param("eventGroup") String eventGroup, @Param("tableGroup") String tableGroup, @Param("lastEventSeqNo") int lastEventSeqNo);

    @Override
    @Delete("DELETE FROM EventGroup_${eventGroup} WHERE TableGroup = #{tableGroup} AND SeqNo <= #{deleteBeforeEventSeqNo}")
    int deleteChgEvents(@Param("eventGroup") String eventGroup, @Param("tableGroup") String tableGroup, @Param("deleteBeforeEventSeqNo") int deleteBeforeEventSeqNo);
}
