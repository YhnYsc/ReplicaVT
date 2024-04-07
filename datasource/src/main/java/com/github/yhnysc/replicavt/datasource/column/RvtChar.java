package com.github.yhnysc.replicavt.datasource.column;

import com.github.yhnysc.replicavt.datasource.api.RvtSupportedColumnType;
import com.github.yhnysc.replicavt.object.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class RvtChar implements RvtSupportedColumnType {
    public static final char ID_CHAR = 'C';
    @Override
    public char identityChar() {
        return ID_CHAR;
    }

    /**
     *
     * @param colSize int
     * @param inputStream {@link InputStream}
     * @return {@link Pair} First is the value in {@link String} which read from Payload,
     *  Second is the offset of the Payload for showing log in case error
     */
    @Override
    public Optional<Pair<String, Integer>> readPayload(int colSize, InputStream inputStream) throws IOException {
        final byte[] buffer = new byte[colSize];
        if(inputStream.read(buffer,0, colSize) > 0) {
            return Optional.of(new Pair<>(new String(buffer), colSize));
        }else{
            return Optional.empty();
        }
    }
}
