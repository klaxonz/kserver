package com.klaxon.kserver.downloader;


public class YtDlpDownloadProgress {

	public final String type;
	public final int percent;
	public final long downloadedBytes;
	public final long totalBytes;
	public final long downloadSpeed;
	public final long eta;
	public final String filepath;

	public YtDlpDownloadProgress() {
		this.type = null;
		this.percent = 0;
		this.downloadedBytes = 0;
		this.totalBytes = 0;
		this.downloadSpeed = 0;
		this.eta = 0;
		this.filepath = "";
	}

	public YtDlpDownloadProgress(String type, int percent, long downloadedBytes, long totalBytes,
								 long downloadSpeed, long eta, String filepath) {
		this.type = type;
		this.percent = percent;
		this.downloadedBytes = downloadedBytes;
		this.totalBytes = totalBytes;
		this.downloadSpeed = downloadSpeed;
		this.eta = eta;
		this.filepath = filepath;
	}

	public String getType() {
		return type;
	}

	public int getPercent() {
		return percent;
	}

	public long getDownloadedBytes() {
		return downloadedBytes;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public long getDownloadSpeed() {
		return downloadSpeed;
	}

	public long getEta() {
		return eta;
	}

	public String getFilepath() {
		return filepath;
	}
}
