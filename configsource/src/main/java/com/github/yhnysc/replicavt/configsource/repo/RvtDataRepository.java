package com.github.yhnysc.replicavt.configsource.repo;

import java.util.List;
import java.util.Optional;

public interface RvtDataRepository<T, K> {
    public void save(T data);
    public void delete(K key);
    public Optional<T> findOne(K key);
    public List<T> findAll(K key);

}
