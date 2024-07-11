package com.klaxon.kserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ConcurrencyConfig {
    @Bean
    public TaskExecutor threadPoolExecutorCpu(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(20);
        executor.setKeepAliveSeconds(10);
        executor.setQueueCapacity(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        executor.initialize();
        return executor;
    }
}