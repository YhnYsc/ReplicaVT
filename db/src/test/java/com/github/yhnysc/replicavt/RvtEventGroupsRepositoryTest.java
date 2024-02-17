package com.github.yhnysc.replicavt;

import com.github.yhnysc.replicavt.api.RvtEventGroupsRepository;
import com.github.yhnysc.replicavt.db.EtcdRepositoryTest;
import com.github.yhnysc.replicavt.db.data.RvtEventGroups;
import com.github.yhnysc.replicavt.db.data.RvtTableGroups;
import com.github.yhnysc.replicavt.db.repo.RvtEventGroupsRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {
        RvtEventGroupsRepositoryImpl.class
})
@EtcdRepositoryTest
public class RvtEventGroupsRepositoryTest {
    @Autowired
    private RvtEventGroupsRepository _eventGroupsRepo;
    @Test
    @DisplayName("SaveFind_1_Record_ExpectedSuccess")
    public void testSaveFindRecordSuccess(){
        RvtTableGroups tableGroup = new RvtTableGroups();
        tableGroup.setTableGroupName("TG1");
        tableGroup.setPriority((short) 1);
        tableGroup.setBatchSize(10);
        tableGroup.addTable("T1");

        RvtEventGroups eventGroup = new RvtEventGroups();
        eventGroup.setEventGroupName("EG1");
        eventGroup.setBulkSize(100);
        eventGroup.addTableGroup(tableGroup);
        _eventGroupsRepo.save(eventGroup);
        Optional<RvtEventGroups> result = _eventGroupsRepo.find("EG1");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("EG1", result.get().getEventGroupName());
        Assertions.assertEquals(100, result.get().getBulkSize());
        Assertions.assertEquals(1, result.get().getTableGroups().size());

        List<RvtTableGroups> tableGroups = result.get().getTableGroups();
        Assertions.assertEquals("TG1", tableGroups.get(0).getTableGroupName());
        Assertions.assertEquals((short) 1, tableGroups.get(0).getPriority());
        Assertions.assertEquals(10, tableGroups.get(0).getBatchSize());

        List<String> tablesInTG = tableGroups.get(0).getTables();
        Assertions.assertEquals(1, tablesInTG.size());
        Assertions.assertEquals("T1", tablesInTG.get(0));
    }

    @Test
    @DisplayName("SaveFindOrderByPriority_5_Record_ExpectedSuccess")
    public void testSaveFindOrderByPriorityRecordSuccess(){
        RvtEventGroups eventGroup = new RvtEventGroups();
        eventGroup.setEventGroupName("EG1");
        eventGroup.setBulkSize(100);
        for(int i=1; i <= 5; i++){
            RvtTableGroups tableGroup = new RvtTableGroups();
            tableGroup.setTableGroupName("TG"+i);
            tableGroup.setPriority((short) i);
            tableGroup.setBatchSize(10+i);
            tableGroup.addTable("T"+i);
            eventGroup.addTableGroup(tableGroup);
        }
        Collections.shuffle(eventGroup.getTableGroups());
        _eventGroupsRepo.save(eventGroup);

        Optional<List<RvtTableGroups>> tableGroupsResult = _eventGroupsRepo.getTableGroupsOrderByPriority("EG1");
        //First element
        List<RvtTableGroups> tableGroups = tableGroupsResult.get();
        Assertions.assertEquals("TG1", tableGroups.get(4).getTableGroupName());
        Assertions.assertEquals((short) 1, tableGroups.get(4).getPriority());
        Assertions.assertEquals(11, tableGroups.get(4).getBatchSize());

        List<String> tablesInTG = tableGroups.get(4).getTables();
        Assertions.assertEquals(1, tablesInTG.size());
        Assertions.assertEquals("T1", tablesInTG.get(0));

        //Last element
        Assertions.assertEquals("TG5", tableGroups.get(0).getTableGroupName());
        Assertions.assertEquals((short) 5, tableGroups.get(0).getPriority());
        Assertions.assertEquals(15, tableGroups.get(0).getBatchSize());

        tablesInTG = tableGroups.get(0).getTables();
        Assertions.assertEquals(1, tablesInTG.size());
        Assertions.assertEquals("T5", tablesInTG.get(0));

    }


}
