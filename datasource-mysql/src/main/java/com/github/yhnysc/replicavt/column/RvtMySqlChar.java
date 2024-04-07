package com.github.yhnysc.replicavt.column;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.datasource.column.RvtChar;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RvtMySqlChar extends RvtChar {
    /** <p>
     *  Format:
     *  </p>
     *   <code>CONCAT('C',RPAD(CAST({column_name} AS CHAR({column_size})), {column_size}, ' '))</code>
     *
     * @param colMetadata   {@link RvtTables.ColMetadata}
     * @param tableAlias    {@link String}
     * @return {@link String}
     */
    @Override
    public String renderToDbTrigger(final RvtTables.ColMetadata colMetadata, String tableAlias) {
        final StringBuffer sb = new StringBuffer("CONCAT(");
        sb.append("'").append(identityChar()).append("'");
        sb.append(",RPAD(CAST(");
        if(StringUtils.hasText(tableAlias)){
            sb.append(tableAlias).append('.').append(colMetadata.getName());
        }else{
            sb.append(colMetadata.getName());
        }
        sb.append(" AS CHAR(").append(colMetadata.getSize()).append("))");
        sb.append(",").append(colMetadata.getSize()).append(",' '))");
        return sb.toString();
    }

    /**
     *  Get the column with the SQL function to retrieve byte of CHAR column's value
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
