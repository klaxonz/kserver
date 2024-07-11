package com.klaxon.kserver.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.of(20000, ChronoUnit.SECONDS))
                .callTimeout(Duration.of(10000, ChronoUnit.SECONDS))
                .readTimeout(Duration.of(20000, ChronoUnit.SECONDS))
                .writeTimeout(Duration.of(10000, ChronoUnit.SECONDS))
                .build();
    }

}
