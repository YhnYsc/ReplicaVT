package com.github.yhnysc.replicavt.svc;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.datasource.svc.RvtDatasourceAccessService;
import com.github.yhnysc.replicavt.object.Pair;
import com.github.yhnysc.replicavt.test.MySqlTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SpringBootTest(classes = {
        RvtDatasourceAccessService.class
})
@MySqlTest
public class RvtDatasourceAccessServiceTest {

    @Autowired
    private RvtDatasourceAccessService _service;

    @Value("${rvt.db.schema}")
    private String _dbSchema;

    @Test
    public void testGetColumnMetadata(){
        final List<RvtTables.ColMetadata> colMetadataList = _service.getColumnMetadata(_dbSchema, "test_table");
        Assertions.assertNotNull(colMetadataList);
        Assertions.assertFalse(colMetadataList.isEmpty());
    }

    @Test
    public void testGetKeyColumnNames(){
        final List<String> keyColumnNames = _service.getKeyColumnNames(_dbSchema, "test_table");
        Assertions.assertNotNull(keyColumnNames);
        Assertions.assertEquals(2, keyColumnNames.size());
    }

    @Test
    public void testGetKeyColumnNamesWithIndexName(){
        final List<String> keyColumnNames = _service.getKeyColumnNames(_dbSchema, "test_table", "PRIMARY");
        Assertions.assertNotNull(keyColumnNames);
        Assertions.assertEquals(2, keyColumnNames.size());
    }

    @Test
    public void testGetParentTable(){
        final List<Pair<String, String>> parentTables = _service.getParentTables(_dbSchema, "test_leaf_table");
        Assertions.assertNotNull(parentTables);
        Assertions.assertEquals(1, parentTables.size());
        Assertions.assertEquals(_dbSchema, parentTables.get(0).getFirst());
        Assertions.assertEquals("test_table", parentTables.get(0).getSecond());
    }

    @Test
    public void testGetLeafTables(){
        final List<Pair<String, String>> leafTables = _service.getLeafTables(_dbSchema, "test_table");
        Assertions.assertNotNull(leafTables);
        Assertions.assertEquals(2, leafTables.size());
        final Comparator<Pair<String, String>> comparator = Comparator
                .comparing(Pair<String,String>::getFirst)
                .thenComparing(Pair::getSecond);
        Collections.sort(new ArrayList<>(leafTables), comparator);
        Assertions.assertEquals(_dbSchema, leafTables.get(0).getFirst());
        Assertions.assertEquals(_dbSchema, leafTables.get(1).getFirst());
        Assertions.assertEquals("test_leaf_table", leafTables.get(0).getSecond());
        Assertions.assertEquals("test_son_table", leafTables.get(1).getSecond());
    }

    @Test
    public void testIsTableExisted(){
        Assertions.assertTrue(_service.isTableExisted(_dbSchema, "test_table"));
    }


}
