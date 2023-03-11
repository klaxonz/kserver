package com.klaxon.kserver.downloader;

import java.util.Map;

public class Request {

    private String url;

    private Map<String, String> header;

    private RequestCallback callback;

    public Request(String url, Map<String, String> header, RequestCallback callback) {
        this.url = url;
        this.header = header;
        this.callback = callback;
    }
}
