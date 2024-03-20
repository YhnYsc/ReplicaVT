package com.github.yhnysc.replicavt.datasource.svc;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.object.Pair;
import com.github.yhnysc.replicavt.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RvtDatasourceAccessService {
    private final JdbcTemplate _jdbcTmpl;
    @Autowired
    public RvtDatasourceAccessService(JdbcTemplate jdbcTmpl){
        _jdbcTmpl = jdbcTmpl;
    }

    /**
     * Use function getMetaData() to obtain the 1) column name; 2) type; and 3) size for the table
     * from the Database
     *
     * @param tableSchema {@link String}
     * @param tableName {@link String}
     * @return
     */
    public List<RvtTables.ColMetadata> getColumnMetadata(String tableSchema, String tableName){
        final List<RvtTables.ColMetadata> colMetadataList = new ArrayList<>();
        try(final Connection dbConnection = _jdbcTmpl.getDataSource().getConnection();){
            final ResultSet resultSet = dbConnection.getMetaData().getColumns(tableSchema, tableSchema, tableName, null);
            while (resultSet.next()){
                final RvtTables.ColMetadata colMetadata = new RvtTables.ColMetadata(
                    resultSet.getString("COLUMN_NAME"),
                    resultSet.getString("TYPE_NAME"),
                    calculateColumnSize(resultSet)
                );
                colMetadataList.add(colMetadata);
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return colMetadataList;
    }

    /**
     * Return the key column meta for the input unique index
     *
     * @param tableSchema {@link String}
     * @param tableName {@link String}
     * @param uniqueIdxName {@link String}
     * @return
     */
    public List<String> getKeyColumnNames(String tableSchema, String tableName, String uniqueIdxName){
        final List<String> keyColumnNameList = new ArrayList<>(1);
        try (final Connection conn = _jdbcTmpl.getDataSource().getConnection();) {
            final ResultSet resultSet = conn.getMetaData().getIndexInfo(tableSchema, tableSchema, tableName, true, false);
            while(resultSet.next()){
                if(uniqueIdxName.equals(resultSet.getString("INDEX_NAME"))){
                    // The column belong to the specified unique index
                    final String keyColumnName = resultSet.getString("COLUMN_NAME");
                    keyColumnNameList.add(keyColumnName);
                }
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return keyColumnNameList;
    }

    /**
     * Return the key column meta for the FIRST unique index
     *
     * @param tableSchema {@link String}
     * @param tableName {@link String}
     * @return
     */
    public List<String> getKeyColumnNames(String tableSchema, String tableName){
        final List<String> keyColumnNameList = new ArrayList<>(1);
        try (final Connection conn = _jdbcTmpl.getDataSource().getConnection();) {
            final ResultSet resultSet = conn.getMetaData().getIndexInfo(tableSchema, tableSchema, tableName, true, false);
            String previousIndexName = null;
            while(resultSet.next()){
                String indexName = resultSet.getString("INDEX_NAME");
                // Index name change as key break
                if(previousIndexName != null && !previousIndexName.equals(indexName)) break;
                previousIndexName = indexName;

                final String keyColumnName = resultSet.getString("COLUMN_NAME");
                keyColumnNameList.add(keyColumnName);
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return keyColumnNameList;
    }

    public List<Pair<String, String>> getParentTables(String tableSchema, String tableName){
        final List<Pair<String, String>> parentTableList = new ArrayList<>();
        try (final Connection conn = _jdbcTmpl.getDataSource().getConnection();) {
            final ResultSet resultSet = conn.getMetaData().getImportedKeys(tableSchema, tableSchema, tableName);
            while(resultSet.next()) {
                final Pair<String, String> parentTable = new Pair<>(
                        ObjectUtil.firstNonNull(resultSet.getString("PKTABLE_SCHEM"), tableSchema),
                        resultSet.getString("PKTABLE_NAME")
                );
                parentTableList.add(parentTable);
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return parentTableList.stream().distinct().collect(Collectors.toUnmodifiableList());
    }

    public List<Pair<String, String>> getLeafTables(String tableSchema, String tableName){
        final List<Pair<String, String>> leafTableList = new ArrayList<>();
        try (final Connection conn = _jdbcTmpl.getDataSource().getConnection();) {
            final ResultSet resultSet = conn.getMetaData().getExportedKeys(tableSchema, tableSchema, tableName);
            while(resultSet.next()) {
                final Pair<String, String> leafTable = new Pair<>(
                        ObjectUtil.firstNonNull(resultSet.getString("FKTABLE_SCHEM"), tableSchema),
                        resultSet.getString("FKTABLE_NAME")
                );
                leafTableList.add(leafTable);
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return leafTableList.stream().distinct().collect(Collectors.toUnmodifiableList());
    }


    public boolean isTableExisted(String tabSchema, String tabName){
        try (final Connection conn = _jdbcTmpl.getDataSource().getConnection();) {
            final ResultSet resultSet = conn.getMetaData().getTables(tabSchema, tabSchema, tabName, null);
            return resultSet.next();
        } catch (final SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    protected int calculateColumnSize(final ResultSet resultSet) throws SQLException {
        final int SIGNED_DIGIT = 1, DOT = 1;
        // For CHAR and VARCHAR types column, the size will be "CHAR_OCT_LENGTH"
        return Math.max(
                resultSet.getInt("COLUMN_SIZE"),
                resultSet.getInt("CHAR_OCTET_LENGTH"))
                //For Fraction Percision
                + (resultSet.getString("DECIMAL_DIGITS") != null ?
                SIGNED_DIGIT + DOT : 0);
    }

}
