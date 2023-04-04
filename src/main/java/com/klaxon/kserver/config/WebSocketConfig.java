package com.klaxon.kserver.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.klaxon.kserver.aop.CustomSpringConfigurator;
import com.klaxon.kserver.filter.WebSocketFilter;

@Configuration
public class WebSocketConfig {

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public CustomSpringConfigurator customSpringConfigurator() {
		return new CustomSpringConfigurator();
	}

	@Bean
	public FilterRegistrationBean<Filter> webSocketFilterRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(webAuthFilter());
		registration.setName("WebSocketFilter");
		registration.addUrlPatterns("/ws/*");
		registration.setOrder(0);
		return registration;
	}

	@Bean
	public Filter webAuthFilter() {
		return new WebSocketFilter();
	}

}
