package com.github.yhnysc.replicavt.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(excludeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
})
public class RvtMain {
    public static void main(String[] args){
        final SpringApplication app = new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .lazyInitialization(true)
                .registerShutdownHook(true)
                .sources(RvtConfig.class)
                .build();
        System.exit(SpringApplication.exit(app.run(args)));
    }

}
