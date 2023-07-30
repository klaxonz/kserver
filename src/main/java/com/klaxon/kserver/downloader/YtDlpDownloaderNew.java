package com.klaxon.kserver.downloader;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.klaxon.kserver.constants.WebPageTaskConstants;
import com.klaxon.kserver.constants.WebPageVideoTaskConstants;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.util.FFmpegExecutor;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class YtDlpDownloaderNew implements Downloader {

    private final Config config;
    private final DownloadCallback callback;
    private final YtDlpExecutorNew executor;

    public YtDlpDownloaderNew(Config config, DownloadCallback callback) {
        this.config = config;
        this.callback = callback;
        this.executor = new YtDlpExecutorNew(this.config);
    }

    @Override
    public void download(String url) {
        try {
            List<VideoInfo> videoInfoList = executor.getVideoInfo(url);

            long videoSize = 0L;
            int duration = 0;
            for (VideoInfo videoInfo : videoInfoList) {
                videoSize += videoInfo.getFilesizeApprox();
                duration  += videoInfo.getDuration();
            }

            WebPageTask webPageTask = new WebPageTask();
            webPageTask.setUserId(ThreadLocalHolder.getUser().getId());
            webPageTask.setWebPageId(ThreadLocalHolder.getWebPage().getId());
            webPageTask.setVideoSize(videoSize);
            webPageTask.setVideoDuration(duration);
            webPageTask.setVideoProgress(0);
            webPageTask.setVideoDownloadedSize(0L);
            webPageTask.setStatus(WebPageTaskConstants.VIDEO_DOWNLOAD_STATUS_DOWNLOADING);
            webPageTask.setType(videoInfoList.size() > 1 ? WebPageTaskConstants.VIDEO_TYPE_PLAYLIST : WebPageTaskConstants.VIDEO_TYPE_SINGLE);

            List<MultiDownloadState> stateList = Lists.newLinkedList();

            for (VideoInfo videoInfo : videoInfoList) {
                String originalUrl = videoInfo.getOriginalUrl();

                String thumbnailNamePattern = videoInfoList.size() > 1
                        ? videoInfo.getPlaylist() + File.separator + "%(title)s.%(ext)s" : "%(title)s" + File.separator + "%(title)s.%(ext)s";
                String thumbnailPath = executor.downloadThumbnail(originalUrl, thumbnailNamePattern);

                String title = videoInfoList.size() > 1 ?  videoInfo.getPlaylist() : videoInfo.getTitle();
                if (Objects.isNull(webPageTask.getFilePath())) {
                    webPageTask.setFilePath(title);
                }

                MultiDownloadState state = callback.onBefore(webPageTask, videoInfo, thumbnailPath);
                stateList.add(state);
            }

            for (int i = 0; i  < videoInfoList.size(); i++) {
                VideoInfo videoInfo = videoInfoList.get(i);
                MultiDownloadState state = stateList.get(i);
                WebPageVideoTask webPageVideoTask = state.getWebPageVideoTask();

                String originalUrl = videoInfo.getOriginalUrl();
                String videoNamePattern = Objects.isNull(videoInfo.getPlaylist())
                        ? "%(title)s" + File.separator + "%(title)s.%(ext)s.video" : videoInfo.getPlaylist() + File.separator + "%(title)s.%(ext)s.video";
                String audioNamePattern = Objects.isNull(videoInfo.getPlaylist())
                        ? "%(title)s" + File.separator + "%(title)s.%(ext)s.audio" : videoInfo.getPlaylist() + File.separator + "%(title)s.%(ext)s.audio";

                String filepath = Strings.EMPTY;
                if (webPageVideoTask.getIsMerge().equals(1)) {
                    videoNamePattern = Objects.isNull(videoInfo.getPlaylist())
                            ? "%(title)s" + File.separator + "%(title)s.%(ext)s" : videoInfo.getPlaylist() + File.separator + "%(title)s.%(ext)s";
                    filepath = executor.downloadFormat(originalUrl, WebPageVideoTaskConstants.DOWNLOAD_TYPE_BEST, videoNamePattern, callback, state);
                    filepath = config.getBasePath() + filepath;
                } else {
                    String videoPath = executor.downloadFormat(originalUrl, WebPageVideoTaskConstants.DOWNLOAD_TYPE_VIDEO, videoNamePattern, callback, state);
                    String audioPath = executor.downloadFormat(originalUrl, WebPageVideoTaskConstants.DOWNLOAD_TYPE_AUDIO, audioNamePattern, callback, state);
                    videoPath = config.getBasePath() + videoPath;
                    audioPath = config.getBasePath() + audioPath;
                    filepath = FFmpegExecutor.mergeVideoAndAudio(videoPath, audioPath);
                }

                long size = FileUtil.size(new File(filepath));

                filepath = filepath.substring(config.getBasePath().length());
                state.getWebPageVideoTask().setFilePath(filepath);
                state.getWebPageVideoTask().setVideoSize(size);

                callback.onDownloadFinish(state);
            }

            if (!stateList.isEmpty()) {
                callback.onTaskFinish(stateList);
            }

        } catch (IOException | InterruptedException e) {
            callback.onException(e);
            throw new RuntimeException(e);
        }
    }

}
