package com.klaxon.kserver.downloader;

import com.klaxon.kserver.property.YtDlpProperty;

import java.io.File;
import java.util.Objects;

public class Config {

    private String basePath;
    private YtDlpProperty property;

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

    public YtDlpProperty getProperty() {
        return property;
    }

    public void setProperty(YtDlpProperty property) {
        this.property = property;
    }
}
