package com.github.yhnysc.replicavt.test;

import com.github.yhnysc.replicavt.datasource.factory.RvtDataSourceFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({
        MybatisAutoConfiguration.class
})
@ContextConfiguration(
        initializers = {MySQLTestcontainersInitializer.class},
        classes = {RvtDataSourceFactory.class}
)
public @interface MySqlTest {
}
