package com.klaxon.kserver.service;

public interface IFileStateService {

    void setProgress(String key, String progress);

    String getProgress(String key);

}
