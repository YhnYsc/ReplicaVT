package com.github.yhnysc.replicavt;


import com.github.yhnysc.replicavt.api.RvtTablesRepository;
import com.github.yhnysc.replicavt.db.EtcdDataSourceConfiguration;
import com.github.yhnysc.replicavt.db.EtcdRepositoryTest;
import com.github.yhnysc.replicavt.db.EtcdTestcontainersInitializer;
import com.github.yhnysc.replicavt.db.GsonConfiguration;
import com.github.yhnysc.replicavt.db.data.RvtTables;
import com.github.yhnysc.replicavt.db.repo.RvtTablesRepositoryImpl;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.test.EtcdClusterExtension;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest(classes = {
        RvtTablesRepositoryImpl.class
})
@EtcdRepositoryTest
public class RvtTablesRepositoryTest {

    @Autowired
    private RvtTablesRepository _tablesRepo;

    @Test
    @DisplayName("SaveFind_1_Record_ExpectedSuccess")
    public void testSaveFindRecordSuccess(){
        RvtTables table = new RvtTables();
        table.setTableSchema("TS1");
        table.setTableName("T1");
        table.setTableVersion(1);
        table.setCreateTimestamp(new Timestamp(Instant.now().toEpochMilli()));
        _tablesRepo.save(table);
        Optional<RvtTables> result = _tablesRepo.find("TS1", "T1", 1);
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
