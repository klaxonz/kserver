package com.klaxon.kserver.downloader;

import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;

import java.util.Map;

public interface YtDlpDownloadCallback {

	void onThumbnailSave(WebPageVideoTask webPageVideoTask, String path);

	void onProgress(WebPageVideoTask webPageVideoTask, YtDlpDownloadProgress progress);

	void onFinish(WebPageVideoTask webPageVideoTask, String filepath);

	void onGetDuration(WebPageVideoTask webPageVideoTask, int duration);

	WebPageVideoTask beforeVideoDownload(Integer videoTpe, Map<String, Object> videoInfo, String thumbnailPath);

	void onGetSize(WebPageVideoTask webPageVideoTask, long size);

	void onGetResolution(WebPageVideoTask webPageVideoTask, int width, int height);

	void onCreatePlaylistTask(Map<String, Object> videoInfo);

	void updateWebPageTask(WebPageTask task);

	void updateWebPageVideoTask(WebPageVideoTask videoTask);

}
