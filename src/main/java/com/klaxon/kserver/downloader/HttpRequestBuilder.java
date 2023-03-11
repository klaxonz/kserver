package com.klaxon.kserver.downloader;

import java.util.Map;

public class HttpRequestBuilder implements RequestBuilder {

    private String url;

    private Map<String, String> header;

    private RequestCallback callback;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    @Override
    public void setCallback(RequestCallback callback) {
        this.callback = callback;
    }

    public Request build() {
        return new Request(url, header, callback);
    }

}
