package com.github.yhnysc.replicavt;

import com.github.yhnysc.replicavt.datasource.api.RvtChangeEventTableDao;
import com.github.yhnysc.replicavt.datasource.entity.RvtChangeEventEntity;
import com.github.yhnysc.replicavt.sql.RvtChangeEventTableMySqlDaoImpl;
import com.github.yhnysc.replicavt.sql.RvtChangeEventTableMySqlMapper;
import com.github.yhnysc.replicavt.test.MySqlTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {
        RvtChangeEventTableMySqlDaoImpl.class,
        RvtChangeEventTableMySqlMapper.class
})
@MySqlTest
public class RvtMySQLEntityGroupTableTest {

    @Autowired
    RvtChangeEventTableDao _dao;

    @Test
    @DisplayName("Create_Table_ExpectedSuccess")
    public void testCreateTable(){
        Assertions.assertDoesNotThrow(()->_dao.createChangeEventTable("MGS12345"));
    }

    @Test
    @DisplayName("Create_Trigger_ExpectedSuccess")
    public void testCreateTrigger(){
        //TODO

    }

    @Test
    @DisplayName("Find_3_Records_ExpectedSuccess")
    public void testFindOutstandingChgEvents(){
        final List<RvtChangeEventEntity> results = _dao.findOutstandingChgEvents("LALILULELO", "TG1",3);
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals(4, results.get(0).getSeqNo());
        Assertions.assertEquals(5, results.get(1).getSeqNo());
        Assertions.assertEquals(6, results.get(2).getSeqNo());
    }

    @Test
    @DisplayName("Delete_3_Records_ExpectedSuccess")
    public void testDeleteChgEvents(){
        final int deletedCount = _dao.deleteChgEvents("LALILULELO", "TG1", 3);
        Assertions.assertEquals(3, deletedCount);
    }

}
