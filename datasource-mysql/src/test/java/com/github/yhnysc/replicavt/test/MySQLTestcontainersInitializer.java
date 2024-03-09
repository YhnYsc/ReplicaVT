package com.github.yhnysc.replicavt.test;

import com.github.yhnysc.replicavt.TestConst;
import com.github.yhnysc.replicavt.datasource.DatabaseEngines;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

public class MySQLTestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static MySQLContainer<?> database = new MySQLContainer<>(TestConst.MYSQL_IMAGE)
            .withUsername(TestConst.DB_USER)
            .withPassword(TestConst.DB_PW)
            .withEnv("MYSQL_ROOT_PASSWORD", TestConst.DB_PW)
            .withInitScript(TestConst.DB_INIT_TABLES_SCRIPT);

    static {
        Startables.deepStart(database).join();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext configurableAppCtx) {
        TestPropertyValues.of(
                "rvt.db.type=" + DatabaseEngines.MYSQL,
                "rvt.db.name=" + database.getDatabaseName(),
                "rvt.db.user=" + database.getUsername(),
                "rvt.db.user.password=" + database.getPassword(),
                "rvt.db.schema=" + database.getDatabaseName(),
                "rvt.db.host=" + database.getHost(),
                "rvt.db.port=" + database.getFirstMappedPort(),
                "rvt.db.ssl.enabled=false"
        ).applyTo(configurableAppCtx.getEnvironment());
    }
}
