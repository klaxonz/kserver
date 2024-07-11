package com.klaxon.kserver.module.media.extractor;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MediaMetaExtractorHandler {

    @Resource
    private List<MediaMetaExtractor> mediaMetaExtractors;

    public MediaMetaExtractor getMediaMetaExtractors(Integer type) {
        return mediaMetaExtractors.stream()
                .filter(mediaMetaExtractor -> mediaMetaExtractor.getType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
