package com.klaxon.kserver.downloader;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.util.Request;
import com.klaxon.kserver.util.RequestBuilder;
import com.klaxon.kserver.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class YoutubeDownloader {

    @Autowired
    private FFMpegCommandExecutor ffmpegCommandExecutor;

    public void download(String url) throws IOException {

        UrlBuilder urlBuilder = UrlBuilder.of(url);
        String videoId = (String) urlBuilder.getQuery().get("v");
        if (StringUtils.isBlank(videoId)) {
            return;
        }
        String videoInfoUrl = "https://youtubei.googleapis.com/youtubei/v1/player?key=" + "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
        String body =
                "{" +
                "  \"videoId\": \"" + videoId + "\"," +
                "  \"context\": {" +
                "    \"client\": {" +
                "      \"hl\": \"en\"," +
                "      \"gl\": \"US\"," +
                "      \"clientName\": \"ANDROID\"," +
                "      \"clientVersion\": \"16.02\"" +
                "    }" +
                "  }" +
                "}";
        String response = HttpUtil.post(videoInfoUrl, body);
        JSON json = JSONUtil.parse(response);
        String title = (String) JSONUtil.getByPath(json, "videoDetails.title");
        List<Map<String, Object>> adaptiveFormats = (List<Map<String, Object>>) JSONUtil.getByPath(json, "streamingData.adaptiveFormats");
        List<Map<String, Object>> videoList = adaptiveFormats.stream()
                .sorted(Comparator.comparingInt(YoutubeDownloader::applyAsInt).reversed())
                .filter(o -> StringUtils.contains((String) o.get("mimeType"), "video"))
                .collect(Collectors.toList());

        List<Map<String, Object>> audioList = adaptiveFormats.stream()
                .sorted(Comparator.comparingInt(YoutubeDownloader::applyAsInt).reversed())
                .filter(o -> StringUtils.contains((String) o.get("mimeType"), "audio"))
                .collect(Collectors.toList());
        Map<String, Object> bestQualityVideo = videoList.get(0);
        Map<String, Object> bestQualityAudio = audioList.get(0);

        String videoDownloadUrl = (String) bestQualityVideo.get("url");
        String audioDownloadUrl = (String) bestQualityAudio.get("url");

        String fileSavePath = "videos\\";
        String videoFilePath = fileSavePath + title + ".mp4";
        String audioFilePath = fileSavePath + title + ".m4a";
        String mergeFilePath = fileSavePath + title + "_merge.mp4";

        com.klaxon.kserver.util.Request videoRequest = new RequestBuilder().url(videoDownloadUrl).listener(new DefaultDownloadListener()).build();
        Request audioRequest = new RequestBuilder().url(audioDownloadUrl).listener(new DefaultDownloadListener()).build();

        HttpDownloadUtil.downloadFile(videoRequest, new File(videoFilePath));
        HttpDownloadUtil.downloadFile(audioRequest, new File(audioFilePath));

        String[] command = new String[]{"-i", videoFilePath, "-i", audioFilePath, "-c:v", "copy", "-c:a", "copy", mergeFilePath};
        ffmpegCommandExecutor.execute(command);

        new File(audioFilePath).delete();
        new File(videoFilePath).delete();
        new File(mergeFilePath).renameTo(new File(videoFilePath));
    }

    private static int applyAsInt(Object o) {
        return (Integer) ((Map<String, Object>) o).get("bitrate");
    }

}
