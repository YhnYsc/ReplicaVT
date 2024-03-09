package com.github.yhnysc.replicavt.api;

import io.etcd.jetcd.ByteSequence;

@FunctionalInterface
public interface RvtEtcdKey {
    String etcdKey();

    static String contructEtcdKey(String... key){
        return String.join(ByteSequence.NAMESPACE_DELIMITER.toString(), key);
    }
}
