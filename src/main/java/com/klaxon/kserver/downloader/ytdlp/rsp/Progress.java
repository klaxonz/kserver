package com.klaxon.kserver.downloader.ytdlp.rsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Progress {

    @JsonProperty("downloaded_bytes")
    private long downloadedBytes;
    @JsonProperty("total_bytes")
    private long totalBytes;
    private String filename;
    private String status;
    private double elapsed;
    @JsonProperty("ctx_id")
    private String ctxId;
    private double speed;
    private String format;

    public long getDownloadedBytes() {
        return downloadedBytes;
    }
    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setElapsed(double elapsed) {
        this.elapsed = elapsed;
    }
    public double getElapsed() {
        return elapsed;
    }

    public void setCtxId(String ctxId) {
        this.ctxId = ctxId;
    }
    public String getCtxId() {
        return ctxId;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public double getSpeed() {
        return speed;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "downloadedBytes=" + downloadedBytes +
                ", totalBytes=" + totalBytes +
                ", filename='" + filename + '\'' +
                ", status='" + status + '\'' +
                ", elapsed=" + elapsed +
                ", ctxId='" + ctxId + '\'' +
                ", speed=" + speed +
                '}';
    }
}