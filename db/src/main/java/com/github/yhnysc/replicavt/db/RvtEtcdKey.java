package com.github.yhnysc.replicavt.db;

import io.etcd.jetcd.ByteSequence;

@FunctionalInterface
public interface RvtEtcdKey {
    String recordKey();

    static String contructEtcdKey(String... key){
        return String.join(ByteSequence.NAMESPACE_DELIMITER.toString(), key);
    }
}
