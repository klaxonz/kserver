package com.klaxon.kserver.downloader.ytdlp.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpHeaders {

    @JsonProperty("User-Agent")
    private String userAgent;
    @JsonProperty("Accept")
    private String accept;
    @JsonProperty("Accept-Language")
    private String acceptLanguage;
    @JsonProperty("Sec-Fetch-Mode")
    private String secFetchMode;
    @JsonProperty("Referer")
    private String referer;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getSecFetchMode() {
        return secFetchMode;
    }

    public void setSecFetchMode(String secFetchMode) {
        this.secFetchMode = secFetchMode;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }
}