package com.klaxon.kserver.downloader;

import java.io.File;
import java.util.Objects;

public class Config {

    private String basePath;

    public Config() {

    }

    public String getBasePath() {
        if (!Objects.isNull(basePath) && !String.valueOf(basePath.charAt(basePath.length() - 1)).equals(File.separator)) {
            basePath += File.separator;
        }
        return basePath;
    }
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
