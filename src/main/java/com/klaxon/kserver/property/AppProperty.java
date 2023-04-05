package com.klaxon.kserver.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Configuration
public class AppProperty {

	@Value("${app.protocol}")
	private String protocol;

	@Value("${app.host}")
	private String host;

	@Value("${app.port}")
	private String port;


	public String getBaseUrl() {
		return protocol + "://" + host + ":" + port + "/";
	}

}
