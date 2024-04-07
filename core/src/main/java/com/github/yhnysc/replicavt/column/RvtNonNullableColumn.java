package com.github.yhnysc.replicavt.column;

import com.github.yhnysc.replicavt.datasource.api.RvtSupportedColumnType;
import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.object.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class RvtNonNullableColumn implements RvtSupportedColumnType {
    final private RvtSupportedColumnType _actualColumn;

    public RvtNonNullableColumn(RvtSupportedColumnType actualColumn){
        _actualColumn = actualColumn;
    }

    @Override
    public char identityChar() {
        return _actualColumn.identityChar();
    }

    @Override
    public String renderToDbTrigger(RvtTables.ColMetadata colMetadata, String tableAlias) {
        return _actualColumn.renderToDbTrigger(colMetadata, tableAlias);
    }

    @Override
    public Optional<Pair<String, Integer>> readPayload(int colSize, InputStream inputStream) throws IOException {
        final byte[] buffer = new byte[IDENTITY_CHAR_LENGTH];
        // Read the column prefix
        if(inputStream.read(buffer,0, IDENTITY_CHAR_LENGTH) <= 0) {
            return Optional.empty();
        }
        final String colPrefix = new String(buffer);
        if (identityChar() != colPrefix.charAt(0)) {
            // reading incorrect column
            return Optional.empty();
        }
        return _actualColumn.readPayload(colSize, inputStream);
    }

    @Override
    public String getBinaryFunctionForSelect(String colName) {
        return _actualColumn.getBinaryFunctionForSelect(colName);
    }
}
