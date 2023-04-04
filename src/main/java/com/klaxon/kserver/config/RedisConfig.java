package com.klaxon.kserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			ObjectMapper objectMapper) {
		// 配置 json 序列化器 - Jackson2JsonRedisSerializer
		Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		jacksonSerializer.setObjectMapper(objectMapper);

		// 创建并配置自定义 RedisTemplateRedisOperator
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		// 将 key 序列化成字符串
		template.setKeySerializer(new StringRedisSerializer());
		// 将 hash 的 key 序列化成字符串
		template.setHashKeySerializer(new StringRedisSerializer());
		// 将 value 序列化成 json
		template.setValueSerializer(jacksonSerializer);
		// 将 hash 的 value 序列化成 json
		template.setHashValueSerializer(jacksonSerializer);
		// 设置连接器
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}
