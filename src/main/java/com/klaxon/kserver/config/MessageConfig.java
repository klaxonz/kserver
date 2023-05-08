package com.klaxon.kserver.config;

import com.klaxon.kserver.listener.RedisUpdateAndAddListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

@Configuration
public class MessageConfig {

    @Resource
    private RedisUpdateAndAddListener redisUpdateAndAddListener;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 监听所有的key的set事件
        container.addMessageListener(redisUpdateAndAddListener, redisUpdateAndAddListener.getTopic());

        return container;
    }

}
