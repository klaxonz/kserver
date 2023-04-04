package com.klaxon.kserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RedisTemplate restTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
		RedisTemplate template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		// 配置序列化方式
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		// key 采用String的序列化方式
		template.setKeySerializer(stringRedisSerializer);
		// hash
		template.setHashKeySerializer(stringRedisSerializer);
		// value
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();

		return template;
	}

}
