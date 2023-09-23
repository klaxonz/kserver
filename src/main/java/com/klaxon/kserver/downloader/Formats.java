/*
   @from 2023 w3xue.com 
*/
package com.klaxon.kserver.downloader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Formats {

    private String url;
    private String ext;
    private double fps;
    private Integer width;
    private Integer height;
    private String acodec;
    private String vcodec;
    private double tbr;
    private String filesize;
    private Integer quality;
    private String protocol;
    private String resolution;
    @JsonProperty("dynamic_range")
    private String dynamicRange;
    @JsonProperty("aspect_ratio")
    private String aspectRatio;
    @JsonProperty("filesize_approx")
    private int filesizeApprox;
    @JsonProperty("http_headers")
    private HttpHeaders httpHeaders;
    @JsonProperty("audio_ext")
    private String audioExt;
    @JsonProperty("video_ext")
    private String videoExt;
    private double vbr;
    private double abr;
    @JsonProperty("format_id")
    private String formatId;
    private String format;
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

    public double getFps() {
        return fps;
    }
    public void setFps(double fps) {
        this.fps = fps;
    }

    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }
    public String getAcodec() {
        return acodec;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }
    public String getVcodec() {
        return vcodec;
    }

    public void setTbr(double tbr) {
        this.tbr = tbr;
    }
    public double getTbr() {
        return tbr;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }
    public String getFilesize() {
        return filesize;
    }

    public Integer getQuality() {
        return quality;
    }
    public void setQuality(Integer quality) {
        this.quality = quality;
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

    public String getDynamicRange() {
        return dynamicRange;
    }
    public void setDynamicRange(String dynamicRange) {
        this.dynamicRange = dynamicRange;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setFilesizeApprox(int filesizeApprox) {
        this.filesizeApprox = filesizeApprox;
    }
    public int getFilesizeApprox() {
        return filesizeApprox;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setAudioExt(String audioExt) {
        this.audioExt = audioExt;
    }
    public String getAudioExt() {
        return audioExt;
    }

    public void setVideoExt(String videoExt) {
        this.videoExt = videoExt;
    }
    public String getVideoExt() {
        return videoExt;
    }

    public double getVbr() {
        return vbr;
    }
    public void setVbr(double vbr) {
        this.vbr = vbr;
    }

    public void setAbr(double abr) {
        this.abr = abr;
    }
    public double getAbr() {
        return abr;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }
    public String getFormatId() {
        return formatId;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    public String getFormat() {
        return format;
    }

}