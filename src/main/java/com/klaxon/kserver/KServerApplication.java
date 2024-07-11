package com.klaxon.kserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@MapperScan({
	"com.klaxon.kserver.module.**.mapper.**",
})
@EnableRetry
@EnableWebMvc
@SpringBootApplication
@EnableConfigurationProperties
public class KServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KServerApplication.class, args);
	}

}
