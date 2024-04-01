package com.github.yhnysc.replicavt.configsource.api;

import com.github.yhnysc.replicavt.configsource.entity.RvtTestEntity;
import com.github.yhnysc.replicavt.configsource.entity.RvtTestEntityKey;
import com.github.yhnysc.replicavt.configsource.repo.RvtTestEntityRepositoryImpl;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.github.yhnysc.replicavt.configsource.test.EtcdRepositoryTest;
import com.github.yhnysc.replicavt.configsource.tran.TxnAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        RvtTestEntityRepositoryImpl.class
})
@EtcdRepositoryTest
@ExtendWith(OutputCaptureExtension.class)
public class RvtRepositoryTest {

    @Autowired
    private RvtTestEntityRepositoryImpl _repo;
    @Autowired
    private RvtEtcdTransactionManager _etcdTransactionManager;

    @Test
    public void testSaveAndRetrieveOneDataFromEtcdCluster(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("KEY1");
        testEntity.setKey2("KEY2");
        _repo.save(testEntity);
        txn.commit();

        final Optional<RvtTestEntity> oResult = _repo.findOne(new RvtTestEntityKey("KEY1", "KEY2"));
        assertThat(output).contains("Retrieved test/KEY1/KEY2 from etcd cluster");
        assertTrue(oResult.isPresent());
    }

    @Test
    public void testSaveAndRetrieveOneDataInTransaction(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("KEY1");
        testEntity.setKey2("KEY2");
        _repo.save(testEntity);
        final Optional<RvtTestEntity> oResult = _repo.findOne(new RvtTestEntityKey("KEY1", "KEY2"));
        txn.commit();

        assertThat(output).contains("Retrieved test/KEY1/KEY2 in transaction");
        assertTrue(oResult.isPresent());
    }

    @Test
    public void testSaveAndRetrieveMultipleDataAllFromEtcdCluster(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("PREFIX_KEY1");
        IntStream.of(1,2,3).forEach( i -> {
                testEntity.setKey2("KEY" + i);
                _repo.save(testEntity);
            }
        );
        txn.commit();

        final List<RvtTestEntity> resultList = _repo.findAll(() -> "PREFIX_KEY1");
        assertEquals(3, resultList.size());
        assertThat(output)
                .contains("Retrieved prefix test/PREFIX_KEY1, count[3] from etcd cluster")
                .contains("Retrieved prefix test/PREFIX_KEY1, count[0] in transaction");
    }

    @Test
    public void testSaveAndRetrieveMultipleDataAllInTransaction(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("PREFIX_KEY1");
        IntStream.of(1,2,3).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        final List<RvtTestEntity> resultList = _repo.findAll(() -> "PREFIX_KEY1");
        txn.commit();

        assertEquals(3, resultList.size());
        assertThat(output).contains("Retrieved prefix test/PREFIX_KEY1, count[3] in transaction");
    }

    /**
     *  Test when the retrieve multiple data are from cluster and transaction, the descend order is correct when merging
     */
    @Test
    public void testSaveAndRetrieveMultipleDataMixFromEtcdClusterAndTransaction_OrderByKeyDesc(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("PREFIX_KEY1");
        IntStream.of(1,3).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        txn.commit();

        IntStream.of(2,4).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        final List<RvtTestEntity> resultList = _repo.findAllOrderByKeyDesc(() -> "PREFIX_KEY1");
        assertEquals(4, resultList.size());
        assertEquals("KEY4",resultList.get(0).getKey2());
        assertEquals("KEY3",resultList.get(1).getKey2());
        assertEquals("KEY2",resultList.get(2).getKey2());
        assertEquals("KEY1",resultList.get(3).getKey2());
        assertThat(output)
                .contains("Retrieved prefix test/PREFIX_KEY1, count[2] from etcd cluster")
                .contains("Retrieved prefix test/PREFIX_KEY1, count[2] in transaction");
    }

    @Test
    public void testSaveAndRetrieveMultipleDataMixFromEtcdClusterAndTransaction_OrderByCreateTimeDesc(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("PREFIX_KEY1");
        IntStream.of(1,3).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        txn.commit();

        IntStream.of(2,4).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        final List<RvtTestEntity> resultList = _repo.findAllOrderByCreateTimeDesc(() -> "PREFIX_KEY1");
        assertEquals(4, resultList.size());
        assertEquals("KEY4",resultList.get(0).getKey2());
        assertEquals("KEY2",resultList.get(1).getKey2());
        assertEquals("KEY3",resultList.get(2).getKey2());
        assertEquals("KEY1",resultList.get(3).getKey2());
        assertThat(output)
                .contains("Retrieved prefix test/PREFIX_KEY1, count[2] from etcd cluster")
                .contains("Retrieved prefix test/PREFIX_KEY1, count[2] in transaction");

    }

    @Test
    public void testDeleteAndRetrieveMultipleDataMixFromEtcdClusterAndTransaction(CapturedOutput output){
        final TxnAdapter txn = _etcdTransactionManager.getTransaction();
        final RvtTestEntity testEntity = new RvtTestEntity();
        testEntity.setKey1("PREFIX_KEY1");
        IntStream.of(1,2,3).forEach( i -> {
                    testEntity.setKey2("KEY" + i);
                    _repo.save(testEntity);
                }
        );
        txn.commit();

        _repo.delete(new RvtTestEntityKey("PREFIX_KEY1", "KEY2"));
        final List<RvtTestEntity> resultList = _repo.findAllOrderByKey(() -> "PREFIX_KEY1");
        assertEquals(2, resultList.size());
        assertEquals("KEY1", resultList.get(0).getKey2());
        assertEquals("KEY3", resultList.get(1).getKey2());
        assertThat(output)
                .contains("Retrieved prefix test/PREFIX_KEY1, count[3] from etcd cluster")
                .contains("Retrieved prefix test/PREFIX_KEY1, count[0] in transaction");
    }
}
