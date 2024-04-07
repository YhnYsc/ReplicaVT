package com.github.yhnysc.replicavt.datasource.api;

import com.github.yhnysc.replicavt.configsource.entity.RvtTables;
import com.github.yhnysc.replicavt.object.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface RvtSupportedColumnType {
    static final int IDENTITY_CHAR_LENGTH = 1;
    char identityChar();
    String renderToDbTrigger(RvtTables.ColMetadata colMetadata, String tableAlias);
    Optional<Pair<String, Integer>> readPayload(int colSize, InputStream inputStream) throws IOException;
    String getBinaryFunctionForSelect(String colName);
}
