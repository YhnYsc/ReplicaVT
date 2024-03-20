package com.github.yhnysc.replicavt.configsource.test;

import com.github.yhnysc.replicavt.configsource.cfg.EtcdDataSourceConfiguration;
import com.github.yhnysc.replicavt.configsource.cfg.GsonConfiguration;
import com.github.yhnysc.replicavt.configsource.svc.RvtEtcdTransactionManager;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(
        initializers = {EtcdTestcontainersInitializer.class},
        classes = {EtcdDataSourceConfiguration.class, GsonConfiguration.class, RvtEtcdTransactionManager.class}
)
public @interface EtcdRepositoryTest {
}
