package com.github.yhnysc.replicavt.repo;

import com.github.yhnysc.replicavt.api.RvtEventGroupsRepository;
import com.github.yhnysc.replicavt.entity.RvtEventGroups;
import com.github.yhnysc.replicavt.entity.RvtEventGroupsKey;
import com.github.yhnysc.replicavt.entity.RvtTableGroups;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class RvtEventGroupsRepositoryImpl extends RvtEtcdBaseRepository<RvtEventGroups, RvtEventGroupsKey> implements RvtEventGroupsRepository {
    @Autowired
    public RvtEventGroupsRepositoryImpl(final Client etcdCli, final Gson gson) { super(etcdCli, gson, RvtEventGroups.class);}

    /**
     * Get the table group list of the event group, order by the table group priority
     *
     * @param eventGroupName {@link String}
     * @return {@link List} of {@link RvtTableGroups}, order by priority (higher priority first)
     */
    @Override
    public Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName) {
        Optional<RvtEventGroups> eventGroupResult = super.findOne(new RvtEventGroupsKey(eventGroupName));
        if(eventGroupResult.isEmpty()){
            return Optional.empty();
        }
        List<RvtTableGroups> tableGroups = eventGroupResult.get().getTableGroups();
        if(tableGroups == null || tableGroups.isEmpty()){
            // Event Group without any table groups
            return Optional.empty();
        }
        tableGroups.sort(Collections.reverseOrder(Comparator.comparing(RvtTableGroups::getPriority)));
        return Optional.of(tableGroups);
    }
}
