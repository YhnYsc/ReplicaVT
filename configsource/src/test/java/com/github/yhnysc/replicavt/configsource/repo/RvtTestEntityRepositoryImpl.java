package com.github.yhnysc.replicavt.configsource.repo;

import com.github.yhnysc.replicavt.configsource.api.RvtEtcdKey;
import com.github.yhnysc.replicavt.configsource.entity.RvtTestEntity;
import com.github.yhnysc.replicavt.configsource.entity.RvtTestEntityKey;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import com.google.gson.Gson;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.GetOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RvtTestEntityRepositoryImpl extends RvtEtcdBaseRepository<RvtTestEntity, RvtTestEntityKey> {

    @Autowired
    public RvtTestEntityRepositoryImpl(Client etcdCli, Gson gson, RvtEtcdTransactionManager etcdTransactionManager) {
        super(etcdCli, gson, etcdTransactionManager, RvtTestEntity.class);
    }

    public List<RvtTestEntity> findAllOrderByCreateTime(final RvtEtcdKey key){
        final GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.CREATE)
                .withSortOrder(GetOption.SortOrder.ASCEND)
                .build();
        return super.findAll(getOption, key);
    }

    public List<RvtTestEntity> findAllOrderByKey(final RvtEtcdKey key){
        final GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.KEY)
                .withSortOrder(GetOption.SortOrder.ASCEND)
                .build();
        return super.findAll(getOption, key);
    }

    public List<RvtTestEntity> findAllOrderByCreateTimeDesc(final RvtEtcdKey key){
        final GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.CREATE)
                .withSortOrder(GetOption.SortOrder.DESCEND)
                .build();
        return super.findAll(getOption, key);
    }

    public List<RvtTestEntity> findAllOrderByKeyDesc(final RvtEtcdKey key){
        final GetOption getOption = GetOption.newBuilder()
                .isPrefix(true)
                .withSortField(GetOption.SortTarget.KEY)
                .withSortOrder(GetOption.SortOrder.DESCEND)
                .build();
        return super.findAll(getOption, key);
    }
}
