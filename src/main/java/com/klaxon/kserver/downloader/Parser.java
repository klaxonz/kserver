package com.klaxon.kserver.downloader;

import java.util.List;

public interface Parser {

    /**
     * 解析链接，获取视频信息
     * @param url   待解析链接
     */
    List<VideoInfo> parse(String url);

}
