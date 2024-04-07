package com.github.yhnysc.replicavt.datasource.column;

import com.github.yhnysc.replicavt.datasource.api.RvtSupportedColumnType;
import com.github.yhnysc.replicavt.object.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public abstract class RvtVarchar implements RvtSupportedColumnType {
    public static final char ID_CHAR = 'V';
    public static final int PAYLOAD_LENGTH_OF_SIZE_STRING = 5;
    @Override
    public char identityChar() {
        return ID_CHAR;
    }

    @Override
    public Optional<Pair<String, Integer>> readPayload(int colSize, InputStream inputStream) throws IOException {
        short colValueLength = -1;
        byte[] buffer = new byte[PAYLOAD_LENGTH_OF_SIZE_STRING];
        // Get the first 5 chars to know the length of data
        if(inputStream.read(buffer,0, PAYLOAD_LENGTH_OF_SIZE_STRING) <= 0) {
            return Optional.empty();
        }
        boolean isParseSizeStringError = false;
        try {
            colValueLength = Short.parseShort(new String(buffer));
        }catch (NumberFormatException nfe){
            log.error("Error when parsing size string for Varchar", nfe);
            isParseSizeStringError = true;
        }
        if (isParseSizeStringError || colValueLength <= 0) {
            return Optional.empty();
        }
        // Get the value by the length read in payload, not the colSize
        buffer = new byte[colValueLength];
        //Get the actual data
        if (inputStream.read(buffer, 0, colValueLength) > 0) {
            return Optional.of(new Pair<>(
                    new String(buffer), PAYLOAD_LENGTH_OF_SIZE_STRING + colValueLength)
            );
        }else {
            return Optional.empty();
        }
    }
}
