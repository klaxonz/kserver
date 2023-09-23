package com.klaxon.kserver.downloader;

import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;

public class MultiDownloadState {

    private VideoInfo videoInfo;
    private WebPageTask webPageTask;
    private WebPageVideoTask webPageVideoTask;

    public MultiDownloadState(VideoInfo videoInfo, WebPageTask webPageTask, WebPageVideoTask webPageVideoTask) {
        this.videoInfo = videoInfo;
        this.webPageTask = webPageTask;
        this.webPageVideoTask = webPageVideoTask;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public WebPageTask getWebPageTask() {
        return webPageTask;
    }

    public void setWebPageTask(WebPageTask webPageTask) {
        this.webPageTask = webPageTask;
    }

    public WebPageVideoTask getWebPageVideoTask() {
        return webPageVideoTask;
    }

    public void setWebPageVideoTask(WebPageVideoTask webPageVideoTask) {
        this.webPageVideoTask = webPageVideoTask;
    }
}
