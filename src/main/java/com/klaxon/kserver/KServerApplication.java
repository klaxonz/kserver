package com.klaxon.kserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@MapperScan(value = "com.klaxon.kserver.mapper")
@EnableWebMvc
@EnableScheduling
@SpringBootApplication
public class KServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KServerApplication.class, args);
	}

}
