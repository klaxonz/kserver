package com.klaxon.kserver.util;

import java.util.Map;

public class RequestBuilder {

    private final Request request;

    public RequestBuilder() {
        request = new Request();
    }

    public RequestBuilder url(String url) {
        request.setUrl(url);
        return this;
    }

    public RequestBuilder header(String name, String value) {
        request.getHeaders().put(name, value);
        return this;
    }

    public RequestBuilder headerPutAll(Map<String, String> header) {
        request.getHeaders().putAll(header);
        return this;
    }

    public RequestBuilder listener(DownloadListener listener) {
        request.setListener(listener);
        return this;
    }

    public Request build() {
        return request;
    }

}
