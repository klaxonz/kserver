package com.klaxon.kserver.downloader;

import lombok.Data;

@Data
public class YtDlpDownloadProgress {

	public final String type;
	public final int percent;
	public final long downloadedBytes;
	public final long totalBytes;
	public final long downloadSpeed;
	public final long eta;
	public final String filepath;
	public final int isMerge;

	public YtDlpDownloadProgress(String type, int isMerge, int percent, long downloadedBytes, long totalBytes,
			long downloadSpeed, long eta, String filepath) {
		this.type = type;
		this.isMerge = isMerge;
		this.percent = percent;
		this.downloadedBytes = downloadedBytes;
		this.totalBytes = totalBytes;
		this.downloadSpeed = downloadSpeed;
		this.eta = eta;
		this.filepath = filepath;
	}

}
