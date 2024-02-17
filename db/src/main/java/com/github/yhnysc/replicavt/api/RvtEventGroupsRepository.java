package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.db.RvtDataRepository;
import com.github.yhnysc.replicavt.db.data.RvtTableGroups;

import java.util.List;
import java.util.Optional;

public interface RvtEventGroupsRepository<RvtEventGroups, K> extends RvtDataRepository<RvtEventGroups, K> {
    Optional<RvtEventGroups> find(String eventGroupName);
    Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName);
}
