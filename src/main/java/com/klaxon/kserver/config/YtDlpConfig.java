package com.klaxon.kserver.config;

import com.klaxon.kserver.downloader.Config;
import com.klaxon.kserver.property.YtDlpProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YtDlpConfig {

    @Bean
    public Config config(YtDlpProperty ytDlpProperty) {
        Config config = new Config();
        config.setBasePath(ytDlpProperty.getDestination());
        return config;
    }

}
