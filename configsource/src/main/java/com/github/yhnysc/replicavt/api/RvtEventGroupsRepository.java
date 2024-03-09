package com.github.yhnysc.replicavt.api;

import com.github.yhnysc.replicavt.entity.RvtEventGroups;
import com.github.yhnysc.replicavt.entity.RvtEventGroupsKey;
import com.github.yhnysc.replicavt.entity.RvtTableGroups;
import com.github.yhnysc.replicavt.repo.RvtDataRepository;

import java.util.List;
import java.util.Optional;

public interface RvtEventGroupsRepository extends RvtDataRepository<RvtEventGroups, RvtEventGroupsKey> {
    Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName);
}
