package com.github.yhnysc.replicavt.db;

import com.github.yhnysc.replicavt.annotation.EtcdPrefix;
import io.etcd.jetcd.ByteSequence;

public interface RvtStruct {
    /**
     * Return ETCD prefix of this data type
     *  (e.g. rvt/<prefix>/<key>)
     * @return Value of <prefix>
     */
    String uniqueKey();

}
