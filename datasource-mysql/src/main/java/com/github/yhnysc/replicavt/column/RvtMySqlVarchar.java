package com.github.yhnysc.replicavt.column;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.datasource.column.RvtVarchar;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class RvtMySqlVarchar extends RvtVarchar {
    /**
     *  <p>
     *  Format:
     *  </p>
     *   <code>CONCAT('V',LPAD(CAST(OCTET_LENGTH({column_name}) AS CHAR(5)),5,'0'),CAST({column_name} AS CHAR({column_size})))</code>
     *
     * @param colMetadata   {@link RvtTables.ColMetadata}
     * @param tableAlias    {@link String}
     * @return
     */
    @Override
    public String renderToDbTrigger(RvtTables.ColMetadata colMetadata, String tableAlias) {
        final String colName = StringUtils.hasText(tableAlias) ? tableAlias+"."+colMetadata.getName() : colMetadata.getName();
        final StringBuffer sb = new StringBuffer("CONCAT(");
        sb.append("'").append(identityChar()).append("'");
        sb.append(",LPAD(CAST(OCTET_LENGTH(").append(colName).append(") AS CHAR(5)),5,'0')");
        sb.append(",CAST(").append(colName).append(" AS CHAR(").append(colMetadata.getSize()).append(")))");
        return sb.toString();
    }

    /**
     *  Get the column with the SQL function to retrieve byte of VARCHAR column's value
     *
     * @param colName   {@link String}
     * @return  {@link String}
     */
    @Override
    public String getBinaryFunctionForSelect(String colName) {
        final StringBuffer sb = new StringBuffer();
        sb.append("BINARY(").append(colName).append(") AS ").append(colName);
        return sb.toString();
    }
}
