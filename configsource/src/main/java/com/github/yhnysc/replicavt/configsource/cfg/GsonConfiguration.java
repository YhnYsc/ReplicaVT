package com.github.yhnysc.replicavt.configsource.cfg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;

@Configuration
public class GsonConfiguration {
    @Bean
    public Gson gson(){
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(OffsetDateTime.class, new JsonTimestampSerializer());
        gsonBuilder.setFieldNamingStrategy(field -> field.getName().replace("_", ""));
        return gsonBuilder.create();
    }
}
