package com.klaxon.kserver.downloader;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlPath;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.util.Request;
import com.klaxon.kserver.util.RequestBuilder;
import com.klaxon.kserver.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BiliBiliDownloader {

    @Autowired
    private FFMpegCommandExecutor ffMpegCommandExecutor;


    public void download(String url) throws IOException {
        // 提取bv号
        UrlPath urlPath = UrlBuilder.of(url).getPath();
        String bvid = urlPath.getSegment(1);

        // 获取cid
        OkHttpClient client = new OkHttpClient();
        String getCidUrl = String.format("https://api.bilibili.com/x/web-interface/view?bvid=%s", bvid);
        okhttp3.Request request = new okhttp3.Request.Builder().url(getCidUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {

        }
        assert response.body() != null;
        String responseJson = response.body().string();
        JSON json = JSONUtil.parse(responseJson);
        Integer cid = (Integer) JSONUtil.getByPath(json, "data.cid");

        // 获取视频下载地址
        String getVideoInfoUrl = String.format("https://api.bilibili.com/x/web-interface/view?cid=%d&bvid=%s", cid,bvid);
        request = new okhttp3.Request.Builder().url(getVideoInfoUrl).build();
        response = client.newCall(request).execute();
        if (!response.isSuccessful()) {

        }
        assert response.body() != null;
        String result = response.body().string();
        JSON resultJSON = JSONUtil.parse(result);
        String title = (String) JSONUtil.getByPath(resultJSON, "data.title");
        title = FilePathUtil.getFileNameByTitle(title);

        String getVideoDownloadInfoUrl = String.format("https://api.bilibili.com/x/player/playurl?cid=%s&bvid=%s&qn=64&fnver=0&fnval=16&fourk=1", cid, bvid);
        request = new okhttp3.Request.Builder().url(getVideoDownloadInfoUrl).header("Cookie", "SESSDATA=9300ddfc%2C1692337670%2C98020%2A21").build();
        response = client.newCall(request).execute();
        if (!response.isSuccessful()) {

        }

        String videoFilePath = "videos\\" + title + ".mp4";
        String audioFilePath = "videos\\" + title + ".mpa";
        String mergeFilePath = "videos\\" + title + "_merge.mp4";
        videoFilePath = getPath(videoFilePath);
        audioFilePath = getPath(audioFilePath);

        // 请求头
        Map<String, String> header = new HashMap<>();
        header.put("Referer", String.format("https://www.bilibili.com/video/%s", bvid));
        header.put("Cookie", "buvid3=2CD1B8A3-30B8-3DE8-1692-377C930C9E1146358infoc; i-wanna-go-back=-1; _uuid=ADD106A1A-54EC-B29B-86AA-110529E72DC5347861infoc; LIVE_BUVID=AUTO9216490785897955; nostalgia_conf=-1; buvid_fp_plain=undefined; hit-dyn-v2=1; CURRENT_BLACKGAP=0; blackside_state=0; is-2022-channel=1; b_nut=100; fingerprint3=97a3212de44d82364c4ffb3d1fb93ba0; DedeUserID=20324829; DedeUserID__ckMd5=fa32aac23e9abf15; b_ut=5; buvid4=903567BF-C8CD-5A17-9650-6360C8AF7A1A48349-022040420-6XlSTAt%2FTbHQRaILe4wf0w%3D%3D; hit-new-style-dyn=0; rpdid=|(k||l|lumuJ0J'uYY)Yu|)kJ; CURRENT_QUALITY=120; CURRENT_FNVAL=4048; header_theme_version=CLOSE; fingerprint=43d124f3e70f8fc73e5835cf150c09db; PVID=1; bp_t_offset_20324829=765917446471155768; b_lsid=F4CB4996_186866A3D90; buvid_fp=43d124f3e70f8fc73e5835cf150c09db; PPA_CI=2c08a7e972a70c9848200b6e0f1728fd; SESSDATA=22329213%2C1692845324%2C9f5fb%2A21; bili_jct=9d037059ab68cda8353b44c65c5ff853; sid=7117ma6f; home_feed_column=4; innersign=1");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        // 下载视频
        String videoInfoJson = response.body().string();
        JSON vieoInfoJSON = JSONUtil.parse(videoInfoJson);
        List<Map<String, Object>> videoInfoList = (List<Map<String, Object>>) JSONUtil.getByPath(vieoInfoJSON, "data.dash.video");
        List<Map<String, Object>> videoSortedlist = videoInfoList.stream()
                .sorted(Comparator.comparingInt(o -> (int)((Map<String, Object>) o).get("id")).reversed())
                .filter(o1 -> StringUtils.contains((CharSequence) o1.get("codecs"), "avc1")).collect(Collectors.toList());
        Map<String, Object> videoDetail = videoSortedlist.get(0);
        String baseUrl = (String) videoDetail.get("baseUrl");

        Request videoRequest = new RequestBuilder()
                .url(baseUrl)
                .headerPutAll(header)
                .listener(new DefaultDownloadListener())
                .build();
        HttpDownloadUtil.downloadFile(videoRequest, new File(videoFilePath));


        // 下载音频
        header = new HashMap<>();
        header.put("Referer", String.format("https://www.bilibili.com/video/%s", bvid));
        header.put("Cookie", "SESSDATA=9300ddfc%2C1692337670%2C98020%2A21");

        List<Map<String, Object>> audioInfoList = (List<Map<String, Object>>) JSONUtil.getByPath(vieoInfoJSON, "data.dash.audio");
        List<Map<String, Object>> audioSortedlist = audioInfoList.stream()
                .sorted(Comparator.comparingInt(o -> (int)((Map<String, Object>) o).get("id")).reversed())
                .collect(Collectors.toList());
        Map<String, Object> audioDetail = audioSortedlist.get(0);
        String audioBaseUrl = (String) audioDetail.get("baseUrl");

        Request audioRequest = new RequestBuilder()
                .url(audioBaseUrl)
                .headerPutAll(header)
                .listener(new DefaultDownloadListener())
                .build();
        HttpDownloadUtil.downloadFile(audioRequest, new File(audioFilePath));

        String[] command = new String[]{"-i", videoFilePath, "-i", audioFilePath, "-c:v", "copy", "-c:a", "copy", mergeFilePath};
        ffMpegCommandExecutor.execute(command);

        new File(audioFilePath).delete();
        new File(videoFilePath).delete();
        new File(mergeFilePath).renameTo(new File(videoFilePath));

    }

    private String getPath(String path) {
        File outputFilePath = new File(path);
        return outputFilePath.getAbsolutePath();
    }


}
