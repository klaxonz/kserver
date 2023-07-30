package com.klaxon.kserver.downloader;

import com.klaxon.kserver.mapper.model.WebPageTask;

import java.util.List;

public interface DownloadCallback {

    MultiDownloadState onBefore(WebPageTask webPageTask, VideoInfo videoInfo, String thumbnailPath);

    void onDownload(Progress progress, MultiDownloadState state);

    void onDownloadFinish(MultiDownloadState state);

    void onTaskFinish(List<MultiDownloadState> stateList);

    void onException(Throwable e);


}
