package com.klaxon.kserver.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class VideoProperty {

    @Value("${video.store-path}")
    private String videoStorePath;

    public String getVideoStorePath() {
        return videoStorePath;
    }

    public void setVideoStorePath(String videoStorePath) {
        this.videoStorePath = videoStorePath;
    }
}
