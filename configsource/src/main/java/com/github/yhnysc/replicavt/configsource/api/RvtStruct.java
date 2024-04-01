package com.github.yhnysc.replicavt.configsource.api;

import java.time.OffsetDateTime;

public interface RvtStruct {
    /**
     * Return ETCD prefix of this data type
     *  (e.g. rvt/<prefix>/<key>)
     * @return Value of <prefix>
     */
    String uniqueKey();
    void setTimestamp(OffsetDateTime time);
    OffsetDateTime getTimestamp();

}
