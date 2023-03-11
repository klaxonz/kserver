package com.klaxon.kserver.downloader;

import java.util.Map;

public interface RequestBuilder {

    void setUrl(String url);

    void setHeader(Map<String, String> header);

    void setCallback(RequestCallback callback);

}
