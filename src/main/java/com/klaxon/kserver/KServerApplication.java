package com.klaxon.kserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.oas.annotations.EnableOpenApi;

@MapperScan(value = "com.klaxon.kserver.mapper")
@EnableWebMvc
@EnableOpenApi
@EnableScheduling
@EnableRedisHttpSession
@SpringBootApplication
public class KServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KServerApplication.class, args);
    }

}
