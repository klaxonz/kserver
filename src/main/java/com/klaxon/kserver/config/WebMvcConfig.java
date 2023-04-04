package com.klaxon.kserver.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.aop.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		converters.add(0, converter);
	}

	@Bean
	public HandlerInterceptor getInterceptor() {
		return new LoginInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> pathPatterns = new ArrayList<>();
		pathPatterns.add("/account/login");
		pathPatterns.add("/account/register");
		pathPatterns.add("/web-page-task/img/*");
		pathPatterns.add("/web-page-task/video/*");
		registry.addInterceptor(getInterceptor()).excludePathPatterns(pathPatterns);
	}

}
