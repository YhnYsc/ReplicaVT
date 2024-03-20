package com.github.yhnysc.replicavt.configsource.cfg;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

@Configuration
public class GsonConfiguration {
    @Bean
    public Gson gson(){
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return f.getName().replace("_", "");
            }
        });
        return gsonBuilder.create();
    }
}
