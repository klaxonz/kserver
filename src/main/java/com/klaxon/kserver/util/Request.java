package com.klaxon.kserver.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Request {

    private String url;
    private Map<String, String> headers;
    private DownloadListener listener;

    public Request() {
        headers = new HashMap<>();
    }

}
