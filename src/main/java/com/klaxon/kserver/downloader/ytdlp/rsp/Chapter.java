package com.klaxon.kserver.downloader.ytdlp.rsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Chapter {

    private String title;
    @JsonProperty("start_time")
    private Integer startTime;
    @JsonProperty("end_time")
    private Integer endTime;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStartTime() {
        return startTime;
    }
    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
}
