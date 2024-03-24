package com.klaxon.kserver.config;

import com.klaxon.kserver.property.TmdbProperty;
import com.uwetrottmann.tmdb2.Tmdb;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TmdbConfig {

    @Bean
    public Tmdb tmdb(TmdbProperty tmdbProperty) {
        return new Tmdb(tmdbProperty.getApiKey());
    }

}
