package com.github.yhnysc.replicavt.configsource.api;

import com.github.yhnysc.replicavt.configsource.entity.RvtEventGroups;
import com.github.yhnysc.replicavt.configsource.entity.RvtEventGroupsKey;
import com.github.yhnysc.replicavt.configsource.entity.RvtTableGroups;
import com.github.yhnysc.replicavt.configsource.repo.RvtDataRepository;

import java.util.List;
import java.util.Optional;

public interface RvtEventGroupsRepository extends RvtDataRepository<RvtEventGroups, RvtEventGroupsKey> {
    Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName);
}
