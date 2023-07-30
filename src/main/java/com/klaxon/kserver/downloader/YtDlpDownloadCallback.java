package com.klaxon.kserver.downloader;

import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.util.VideoResolution;

import java.util.Map;

public interface YtDlpDownloadCallback {

	WebPageVideoTask beforeVideoDownload(Integer videoTpe, Map<String, Object> videoInfo, String thumbnailPath);

	void onGetResolution(WebPageVideoTask webPageVideoTask, VideoResolution resolution);

	void updateWebPageTask(WebPageTask task);

	void updateWebPageVideoTask(WebPageVideoTask videoTask);

}
