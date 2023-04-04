package com.klaxon.kserver.downloader;

public interface YtDlpDownloadCallback {

	void onThumbnailSave(String path);

	void onProgress(YtDlpDownloadProgress progress);

	void onFinish(String filepath);

	void onGetDuration(int duration);

	void beforeVideoDownload();

	void onGetSize(long size);

	void onGetResolution(int width, int height);

}
