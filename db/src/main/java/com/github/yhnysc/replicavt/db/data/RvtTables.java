package com.github.yhnysc.replicavt.db.data;

import com.github.yhnysc.replicavt.annotation.EtcdPrefix;
import com.github.yhnysc.replicavt.db.RvtStruct;
import io.etcd.jetcd.ByteSequence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@EtcdPrefix("tables")
public class RvtTables implements RvtStruct, Serializable {

    private String _tableSchema;
    private String _tableName;
    private Integer _tableVersion;
    private List<ColMetadata> _keyColMetadata;
    private List<ColMetadata> _plainColMetadata;
    private List<ColMetadata> _lobColMetadata;
    private List<ColMetadata> _cnfColMetadata;
    private String _cnfRule;
    private Timestamp _createTimestamp;

    @Override
    public String uniqueKey() {
        // tables/sampleSch/sample/v1 = Version 1 of table "sample"
        return String.join(ByteSequence.NAMESPACE_DELIMITER.toString(), _tableSchema, _tableName, String.valueOf(_tableVersion));
    }

    public void addKeyCol(String name, String type, int size){
        if(_keyColMetadata == null){
            _keyColMetadata = new ArrayList<>();
        }
        _keyColMetadata.add(new ColMetadata(name, type, size));
    }
    public void addPlainCol(String name, String type, int size){
        if(_plainColMetadata == null){
            _plainColMetadata = new ArrayList<>();
        }
        _plainColMetadata.add(new ColMetadata(name, type, size));
    }
    public void addLobCol(String name, String type, int size){
        if(_lobColMetadata == null){
            _lobColMetadata = new ArrayList<>();
        }
        _lobColMetadata.add(new ColMetadata(name, type, size));
    }
    public void addCnfCol(String name, String type, int size){
        if(_cnfColMetadata == null){
            _cnfColMetadata = new ArrayList<>();
        }
        _cnfColMetadata.add(new ColMetadata(name, type, size));
    }


    public String getTableSchema() {
        return _tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        _tableSchema = tableSchema;
    }

    public String getTableName() {
        return _tableName;
    }

    public void setTableName(String tableName) {
        _tableName = tableName;
    }

    public int getTableVersion() {
        return _tableVersion;
    }

    public void setTableVersion(int tableVersion) {
        _tableVersion = tableVersion;
    }

    public List<ColMetadata> getKeyColMetadata() {
        return _keyColMetadata;
    }

    public void setKeyColMetadata(List<ColMetadata> keyColMetadata) {
        _keyColMetadata = keyColMetadata;
    }

    public List<ColMetadata> getPlainColMetadata() {
        return _plainColMetadata;
    }

    public void setPlainColMetadata(List<ColMetadata> plainColMetadata) {
        _plainColMetadata = plainColMetadata;
    }

    public List<ColMetadata> getLobColMetadata() {
        return _lobColMetadata;
    }

    public void setLobColMetadata(List<ColMetadata> lobColMetadata) {
        _lobColMetadata = lobColMetadata;
    }

    public List<ColMetadata> getCnfColMetadata() {
        return _cnfColMetadata;
    }

    public void setCnfColMetadata(List<ColMetadata> cnfColMetadata) {
        _cnfColMetadata = cnfColMetadata;
    }

    public String getCnfRule() {
        return _cnfRule;
    }

    public void setCnfRule(String cnfRule) {
        _cnfRule = cnfRule;
    }

    public Timestamp getCreateTimestamp() {
        return _createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        _createTimestamp = createTimestamp;
    }

    static class ColMetadata{
        private String _name;
        private String _type;
        private int _size;

        public ColMetadata(){}

        public ColMetadata(String name, String type, int size){
            _name = name;
            _type = type;
            _size = size;
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            _name = name;
        }

        public String getType() {
            return _type;
        }

        public void setType(String type) {
            _type = type;
        }

        public int getSize() {
            return _size;
        }

        public void setSize(int size) {
            _size = size;
        }
    }
}
