package com.klaxon.kserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.aop.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
		pathPatterns.add("/doc.html");
		pathPatterns.add("doc.html");
		pathPatterns.add("/webjars/**");
		pathPatterns.add("/swagger-resources");
		pathPatterns.add("/error");
		registry.addInterceptor(getInterceptor()).excludePathPatterns(pathPatterns);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
