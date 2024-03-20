package com.github.yhnysc.replicavt.configsource.test;

import io.etcd.jetcd.launcher.EtcdContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.lifecycle.Startables;

import java.util.ArrayList;

public class EtcdTestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static EtcdContainer etcd = new EtcdContainer(TestConst.ETCD_IMAGE, getNodeName(),new ArrayList<>());

    static {
        Startables.deepStart(etcd).join();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext configurableAppCtx) {
        TestPropertyValues.of(
                String.join("=","etcd-endpoints",
                        etcd.clientEndpoint().getHost() + ":" + etcd.clientEndpoint().getPort()),
                String.join("=", "etcd-namespace", TestConst.ETCD_NAMESPACE)
        ).applyTo(configurableAppCtx.getEnvironment());
    }

    private static String getNodeName(){
        return "ETCD_"+ Thread.currentThread().getId();
    }
}
