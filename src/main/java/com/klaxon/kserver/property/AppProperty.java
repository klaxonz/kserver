package com.klaxon.kserver.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
