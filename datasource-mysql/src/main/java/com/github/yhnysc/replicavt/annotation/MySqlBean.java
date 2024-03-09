package com.github.yhnysc.replicavt.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@ConditionalOnExpression("T(com.github.yhnysc.replicavt.datasource.DatabaseEngines).MYSQL.toString().equals('${rvt.db.type}')")
public @interface MySqlBean {
}
