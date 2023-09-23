package com.klaxon.kserver.downloader;

public class Thumbnails {

    private String id;
    private String url;
    private Integer width;
    private Integer height;
    private String resolution;
    private Integer preference;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getResolution() {
        return resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getPreference() {
        return preference;
    }
    public void setPreference(Integer preference) {
        this.preference = preference;
    }
}