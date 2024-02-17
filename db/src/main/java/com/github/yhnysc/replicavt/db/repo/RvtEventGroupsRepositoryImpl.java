package com.github.yhnysc.replicavt.db.repo;

import com.github.yhnysc.replicavt.api.RvtEventGroupsRepository;
import com.github.yhnysc.replicavt.db.RvtEtcdBaseRepository;
import com.github.yhnysc.replicavt.db.RvtEtcdKey;
import com.github.yhnysc.replicavt.db.data.RvtEventGroups;
import com.github.yhnysc.replicavt.db.data.RvtTableGroups;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class RvtEventGroupsRepositoryImpl extends RvtEtcdBaseRepository<RvtEventGroups, RvtEtcdKey> implements RvtEventGroupsRepository<RvtEventGroups, RvtEtcdKey> {
    @Autowired
    public RvtEventGroupsRepositoryImpl(final Client etcdCli, final Gson gson) { super(etcdCli, gson, RvtEventGroups.class);}

    @Override
    public Optional<RvtEventGroups> find(String eventGroupName) {
        return super.findOne(()-> eventGroupName);
    }

    /**
     * Get the table group list of the event group, order by the table group priority
     *
     * @param eventGroupName {@link String}
     * @return {@link List} of {@link RvtTableGroups}, order by priority (higher priority first)
     */
    @Override
    public Optional<List<RvtTableGroups>> getTableGroupsOrderByPriority(String eventGroupName) {
        Optional<RvtEventGroups> eventGroupResult = super.findOne(()-> eventGroupName);
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
