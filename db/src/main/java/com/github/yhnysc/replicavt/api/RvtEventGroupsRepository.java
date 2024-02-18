package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.db.RvtDataRepository;
import com.github.yhnysc.replicavt.db.data.RvtEventGroups;
import com.github.yhnysc.replicavt.db.data.RvtEventGroupsKey;
import com.github.yhnysc.replicavt.db.data.RvtTableGroups;

import java.util.List;
import java.util.Optional;

public interface RvtEventGroupsRepository extends RvtDataRepository<RvtEventGroups, RvtEventGroupsKey> {
    Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName);
}
