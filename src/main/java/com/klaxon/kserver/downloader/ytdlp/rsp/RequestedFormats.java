package com.klaxon.kserver.downloader.ytdlp.rsp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestedFormats {

    private String url;
    private String ext;
    private double fps;
    private int width;
    private int height;
    private String vcodec;
    private String acodec;
    private double tbr;
    private long filesize;
    private int quality;
    private String format;
    private String protocol;
    private String resolution;
    @JsonProperty("dynamic_range")
    private String dynamicRange;
    @JsonProperty("aspect_ratio")
    private double aspectRatio;
    @JsonProperty("filesize_approx")
    private long filesizeApprox;
    @JsonProperty("http_headers")
    private HttpHeaders httpHeaders;
    @JsonProperty("video_ext")
    private String videoExt;
    @JsonProperty("audio_ext")
    private String audioExt;
    private double vbr;
    private double abr;
    @JsonProperty("format_id")
    private String formatId;

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
    public String getExt() {
        return ext;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }
    public double getFps() {
        return fps;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getHeight() {
        return height;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }
    public String getVcodec() {
        return vcodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }
    public String getAcodec() {
        return acodec;
    }

    public void setTbr(double tbr) {
        this.tbr = tbr;
    }
    public double getTbr() {
        return tbr;
    }

    public long getFilesize() {
        return filesize;
    }
    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
    public int getQuality() {
        return quality;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    public String getFormat() {
        return format;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public String getResolution() {
        return resolution;
    }

    public void setDynamicRange(String dynamicRange) {
        this.dynamicRange = dynamicRange;
    }
    public String getDynamicRange() {
        return dynamicRange;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    public double getAspectRatio() {
        return aspectRatio;
    }

    public long getFilesizeApprox() {
        return filesizeApprox;
    }
    public void setFilesizeApprox(long filesizeApprox) {
        this.filesizeApprox = filesizeApprox;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setVideoExt(String videoExt) {
        this.videoExt = videoExt;
    }
    public String getVideoExt() {
        return videoExt;
    }

    public void setAudioExt(String audioExt) {
        this.audioExt = audioExt;
    }
    public String getAudioExt() {
        return audioExt;
    }

    public void setVbr(double vbr) {
        this.vbr = vbr;
    }
    public double getVbr() {
        return vbr;
    }

    public double getAbr() {
        return abr;
    }
    public void setAbr(double abr) {
        this.abr = abr;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }
    public String getFormatId() {
        return formatId;
    }

}