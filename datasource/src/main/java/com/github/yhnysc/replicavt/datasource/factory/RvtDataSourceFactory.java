package com.github.yhnysc.replicavt.datasource.factory;

import com.github.yhnysc.replicavt.configsource.entity.RvtNodeConfig;
import com.github.yhnysc.replicavt.datasource.DatabaseEngines;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@MapperScan(value = "com.github.yhnysc.replicavt.sql", lazyInitialization = "true")
@Lazy
@EnableTransactionManagement
@Configuration
public class RvtDataSourceFactory {
    @Value("${rvt.db.type:}")
    private String _dbType;
    @Value("${rvt.db.name:}")
    private String _dbName;
    @Value("${rvt.db.schema:}")
    private String _dbSchema;
    @Value("${rvt.db.user:}")
    private String _dbUser;
    @Value("${rvt.db.user.password:}")
    private String _dbPassword;
    @Value("${rvt.db.host:}")
    private String _dbHost;
    @Value("${rvt.db.port:}")
    private Integer _dbPort;
    @Value("${rvt.db.ssl.enabled:false}")
    private boolean _dbSslEnabled;

    @Bean(name = "dataSource")
    public DataSource dataSource(@Autowired(required = false) RvtNodeConfig dataSourcePropFromConfig){
        if (dataSourcePropFromConfig != null){
            _dbType = dataSourcePropFromConfig.getDbType();
            _dbName = dataSourcePropFromConfig.getDbName();
            _dbPort = Optional.ofNullable(dataSourcePropFromConfig.getDbPort())
                    .filter(Objects::nonNull).map(Integer::valueOf).orElse(null);
            _dbSchema = dataSourcePropFromConfig.getDbSchema();
            _dbUser = dataSourcePropFromConfig.getDbUser();
            _dbPassword = dataSourcePropFromConfig.getDbPassword();
            _dbHost = dataSourcePropFromConfig.getDbHost();
            _dbSslEnabled = dataSourcePropFromConfig.isDbSslEnabled();
        }
        final DatabaseEngines dbEngine = DatabaseEngines.valueOf(_dbType.toUpperCase());
        final StringBuilder jdbcUrlSB = new StringBuilder();
        jdbcUrlSB.append("jdbc:").append(dbEngine.getJdbcUrl()).append("://");
        jdbcUrlSB.append(_dbHost).append(":").append(_dbPort).append("/").append(_dbName);

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(_dbUser);
        hikariConfig.setPassword(_dbPassword);
        hikariConfig.setSchema(_dbSchema);
        hikariConfig.setJdbcUrl(jdbcUrlSB.toString());
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(_dbSslEnabled));
        hikariConfig.setAutoCommit(false);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "jdbcTmpl")
    public JdbcTemplate JdbcTmpl(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
