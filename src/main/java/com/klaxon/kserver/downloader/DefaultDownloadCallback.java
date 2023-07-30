package com.klaxon.kserver.downloader;

import com.klaxon.kserver.constants.WebPageTaskConstants;
import com.klaxon.kserver.constants.WebPageVideoTaskConstants;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
public class DefaultDownloadCallback implements DownloadCallback {

    private final WebPageTaskMapper webPageTaskMapper;
    private final WebPageVideoTaskMapper webPageVideoTaskMapper;

    @Autowired
    public DefaultDownloadCallback(
            WebPageTaskMapper webPageTaskMapper,
            WebPageVideoTaskMapper webPageVideoTaskMapper
    ) {
        this.webPageTaskMapper = webPageTaskMapper;
        this.webPageVideoTaskMapper = webPageVideoTaskMapper;
    }

    @Override
    @Transactional
    public MultiDownloadState onBefore(WebPageTask webPageTask, VideoInfo videoInfo, String thumbnailPath) {

        if (Objects.isNull(webPageTask.getId())) {
            webPageTaskMapper.insert(webPageTask);
        }

        List<RequestedFormats> requestedFormats = videoInfo.getRequestedFormats();

        // 生成 WebPageVideoTask
        WebPageVideoTask webPageVideoTask = new WebPageVideoTask();
        long filesize, videoFilesize, audioFilesize = 0L;
        if (Objects.isNull(requestedFormats)) {
            filesize = videoInfo.getFilesizeApprox();
            videoFilesize = videoInfo.getFilesizeApprox();
            webPageVideoTask.setIsMerge(1);
        } else {
            RequestedFormats audioFormat = requestedFormats.get(1);
            RequestedFormats videoFormat = requestedFormats.get(0);
            videoFilesize = videoFormat.getFilesize() > 0 ? videoFormat.getFilesize() : videoFormat.getFilesizeApprox();
            audioFilesize = audioFormat.getFilesize() > 0 ? audioFormat.getFilesize() : audioFormat.getFilesizeApprox();
            filesize = videoFilesize + audioFilesize;
            webPageVideoTask.setIsMerge(0);
        }


        webPageVideoTask.setUserId(webPageTask.getUserId());
        webPageVideoTask.setTaskId(webPageTask.getId());
        webPageVideoTask.setWebPageId(webPageTask.getWebPageId());
        webPageVideoTask.setThumbnailPath(thumbnailPath);
        webPageVideoTask.setVideoLength(videoFilesize);
        webPageVideoTask.setAudioLength(audioFilesize);
        webPageVideoTask.setWidth(videoInfo.getWidth());
        webPageVideoTask.setHeight(videoInfo.getHeight());
        webPageVideoTask.setProgress(0);
        webPageVideoTask.setVideoSize(filesize);
        webPageVideoTask.setVideoDownloadedLength(0L);
        webPageVideoTask.setAudioDownloadedLength(0L);
        webPageVideoTask.setVideoIndex(videoInfo.getPlaylistIndex());
        webPageVideoTask.setTitle(videoInfo.getTitle());
        webPageVideoTask.setVideoDuration((int)videoInfo.getDuration());
        webPageVideoTaskMapper.insert(webPageVideoTask);

        return new MultiDownloadState(videoInfo, webPageTask, webPageVideoTask);
    }

    @Override
    @Transactional
    public void onDownload(Progress progress, MultiDownloadState state) {
        WebPageVideoTask webPageVideoTask = state.getWebPageVideoTask();

        // 更新 webPageVideoTask 的进度
        if (Objects.equals(progress.getFormat(), WebPageVideoTaskConstants.DOWNLOAD_TYPE_VIDEO)) {
            webPageVideoTask.setVideoDownloadedLength(progress.getDownloadedBytes());
        } else if (Objects.equals(progress.getFormat(), WebPageVideoTaskConstants.DOWNLOAD_TYPE_AUDIO)) {
            webPageVideoTask.setAudioDownloadedLength(progress.getDownloadedBytes());
        } else {
            webPageVideoTask.setVideoDownloadedLength(progress.getDownloadedBytes());
        }

        long totalSize = webPageVideoTask.getVideoLength() + webPageVideoTask.getAudioLength();
        long downloadedSize = webPageVideoTask.getVideoDownloadedLength() + webPageVideoTask.getAudioDownloadedLength();
        int downloadProgress = (int)((downloadedSize / (double)totalSize) * 100);

        // 视频大小在变大
        long oldVideoSize = webPageVideoTask.getVideoSize();
        if (Objects.equals(progress.getFormat(), WebPageVideoTaskConstants.DOWNLOAD_TYPE_VIDEO)) {
            if (progress.getTotalBytes() > webPageVideoTask.getVideoSize()) {
                webPageVideoTask.setVideoLength(progress.getTotalBytes());
                Integer currentProgress = webPageVideoTask.getProgress();
                totalSize = webPageVideoTask.getVideoLength() + webPageVideoTask.getAudioLength();
                int newDownloadProgress = (int)((downloadedSize / (double)totalSize) * 100);
                if (newDownloadProgress > currentProgress) {
                    downloadProgress = newDownloadProgress;
                }
            }
        }
        webPageVideoTask.setProgress(downloadProgress);
        webPageVideoTaskMapper.updateById(webPageVideoTask);

        // 更新总进度
        WebPageTask task = webPageTaskMapper.selectById(webPageVideoTask.getTaskId());
        Long videoDownloadedSize = webPageVideoTaskMapper.queryVideoDownloadedSize(task.getId());
        Long oldTotalVideoSize = task.getVideoSize();
        long videoSize = task.getVideoSize();
        Integer videoDownloadProgress = (int)((videoDownloadedSize / (double)videoSize) * 100);

        if (progress.getTotalBytes() > webPageVideoTask.getVideoSize()) {
            videoSize = oldTotalVideoSize - oldVideoSize + progress.getTotalBytes();
            Integer newVideoDownloadProgress = (int)((videoDownloadedSize / (double)videoSize) * 100);
            Integer videoProgress = task.getVideoProgress();
            if (videoDownloadProgress > videoProgress) {
                videoDownloadProgress = newVideoDownloadProgress;
            }
        }

        task.setVideoProgress(videoDownloadProgress);
        task.setVideoDownloadedSize(videoDownloadedSize);
        webPageTaskMapper.updateById(task);
    }

    @Override
    public void onDownloadFinish(MultiDownloadState state) {
        WebPageVideoTask webPageVideoTask = state.getWebPageVideoTask();
        webPageVideoTask.setProgress(100);
        webPageVideoTaskMapper.updateById(webPageVideoTask);
    }

    @Override
    @Transactional
    public void onTaskFinish(List<MultiDownloadState> stateList) {
        WebPageTask webPageTask = stateList.get(0).getWebPageTask();
        webPageTask.setStatus(WebPageTaskConstants.VIDEO_DOWNLOAD_STATUS_COMPLETE);
        webPageTask.setVideoProgress(100);
        webPageTaskMapper.updateById(webPageTask);
    }

    @Override
    public void onException(Throwable e) {

    }

}
