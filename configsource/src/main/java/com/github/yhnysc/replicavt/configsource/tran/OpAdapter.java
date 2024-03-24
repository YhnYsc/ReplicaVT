package com.github.yhnysc.replicavt.configsource.tran;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Builder(toBuilder = true)
@Accessors(prefix="_")
@Getter
public class OpAdapter {
    private Op.Type _opType;
    private ByteSequence _key;
    private ByteSequence _value;
    private PutOption _putOption;
    private DeleteOption _deleteOption;
    private GetOption _getOption;
    private Instant _opTime;

    public Op toOp(){
        if(_opType == null){
            throw new IllegalArgumentException("Operation Type shouldn't be null!");
        }
        switch(_opType){
            case PUT:
                return Op.put(_key, _value, _putOption == null ? PutOption.DEFAULT : _putOption);
            case DELETE_RANGE:
                return Op.delete(_key, _deleteOption == null ? DeleteOption.DEFAULT : _deleteOption);
            case RANGE:
                return Op.get(_key, _getOption == null ? GetOption.DEFAULT : _getOption);
            default:
                //TODO: Support TxnOp
                return null;
        }
    }

    public boolean isPut(){
        return Op.Type.PUT == _opType;
    }

    public boolean isDelete(){
        return Op.Type.DELETE_RANGE == _opType;
    }

    public static OpAdapter put(ByteSequence key, ByteSequence value, PutOption putOption){
        return OpAdapter.builder()
                .opType(Op.Type.PUT)
                .key(key)
                .value(value)
                .putOption(putOption)
                .opTime(Instant.now())
                .build();
    }

    public static OpAdapter delete(ByteSequence key, DeleteOption deleteOption){
        return OpAdapter.builder()
                .opType(Op.Type.DELETE_RANGE)
                .key(key)
                .deleteOption(deleteOption)
                .opTime(Instant.now())
                .build();
    }
}
