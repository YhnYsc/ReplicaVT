package com.github.yhnysc.replicavt.configsource.api;


import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.github.yhnysc.replicavt.configsource.test.EtcdRepositoryTest;
import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.configsource.entity.RvtTablesKey;
import com.github.yhnysc.replicavt.configsource.repo.RvtTablesRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@SpringBootTest(classes = {
        RvtTablesRepositoryImpl.class
})
@EtcdRepositoryTest
public class RvtTablesRepositoryTest {

    @Autowired
    private RvtTablesRepository _tablesRepo;
    @Autowired
    private RvtEtcdTransactionManager _etcdTransactionManager;

    @Test
    @DisplayName("SaveFind_1_Record_ExpectedSuccess")
    public void testSaveFindRecordSuccess(){
        RvtTables table = new RvtTables();
        table.setTableSchema("TS1");
        table.setTableName("T1");
        table.setTableVersion(1);
        table.setCreateTimestamp(new Timestamp(Instant.now().toEpochMilli()));
        _tablesRepo.save(table);
        Optional<RvtTables> result = _tablesRepo.findOne(new RvtTablesKey("TS1", "T1", 1));
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("TS1", result.get().getTableSchema());
        Assertions.assertEquals("T1", result.get().getTableName());
        Assertions.assertEquals(1, result.get().getTableVersion());
    }

    @Test
    @DisplayName("SaveFindLatest_1_Record_ExpectedSuccess")
    public void testSaveFindLatestRecordSuccess(){
        RvtTables table = new RvtTables();
        for(int i=1; i <= 5; i++) {
            table.setTableSchema("TS1");
            table.setTableName("T1");
            table.setTableVersion(i);
            table.setCreateTimestamp(new Timestamp(Instant.now().toEpochMilli()));
            _tablesRepo.save(table);
        }
        Optional<RvtTables> result = _tablesRepo.findLatestTableVer("TS1", "T1");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("TS1", result.get().getTableSchema());
        Assertions.assertEquals("T1", result.get().getTableName());
        Assertions.assertEquals(5, result.get().getTableVersion());
    }

}
