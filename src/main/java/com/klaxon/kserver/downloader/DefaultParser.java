package com.klaxon.kserver.downloader;

import java.io.IOException;
import java.util.List;

public class DefaultParser implements Parser {

    private final YtDlpExecutorNew executor;

    public DefaultParser(YtDlpExecutorNew executor) {
        this.executor = executor;
    }

    @Override
    public List<VideoInfo> parse(String url) {
        try {
            return executor.getVideoInfo(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
