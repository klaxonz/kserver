package com.klaxon.kserver.service.impl;

import com.klaxon.kserver.service.IFileStateService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "cacheManager")
public class FileStateServiceImpl implements IFileStateService {


    @Override
    @CachePut(key = "#key", value = "progress")
    public void setProgress(String key, String progress) {

    }

    @Override
    public String getProgress(String key) {
        return null;
    }
}
