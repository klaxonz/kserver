package com.klaxon.kserver.downloader;

import com.klaxon.kserver.downloader.ytdlp.rsp.Progress;
import com.klaxon.kserver.downloader.ytdlp.rsp.VideoInfo;
import com.klaxon.kserver.mapper.model.WebPageTask;

import java.util.List;

public interface DownloadCallback {

    DownloadState onBefore(WebPageTask webPageTask, VideoInfo videoInfo, String thumbnailPath);

    void onDownload(Progress progress, DownloadState state);

    void onDownloadFinish(DownloadState state);

    void onTaskFinish(List<DownloadState> stateList);

    void onException(Throwable e);


}
