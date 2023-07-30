package com.klaxon.kserver.downloader;

import cn.hutool.core.lang.Assert;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class YtDlpExecutorNewTest {

    @Resource
    private final WebPageTaskMapper webPageTaskMapper;
    @Resource
    private final WebPageVideoTaskMapper webPageVideoTaskMapper;

    YtDlpExecutorNewTest(WebPageTaskMapper webPageTaskMapper, WebPageVideoTaskMapper webPageVideoTaskMapper) {
        this.webPageTaskMapper = webPageTaskMapper;
        this.webPageVideoTaskMapper = webPageVideoTaskMapper;
    }

    @Test
    public void testIsSupported() throws IOException, InterruptedException {
        YtDlpExecutorNew executor = new YtDlpExecutorNew(new Config());

        String url1 = "https://www.bilibili.com/video/BV1U94y1q7hF?spm_id_from=333.1007.tianma.1-1-1.click";
        boolean res1 = executor.isSupported(url1);
        Assert.equals(res1, true);

        String url2 = "https://poe.com/Sage";
        boolean res2 = executor.isSupported(url2);
        Assert.equals(res2, false);
    }

    @Test
    public void testGetVideoInfo() throws IOException, InterruptedException {
        String url = "https://www.bilibili.com/video/BV1U94y1q7hF?spm_id_from=333.1007.tianma.1-1-1.click";
        YtDlpExecutorNew executor = new YtDlpExecutorNew(new Config());
        List<VideoInfo> videoInfo = executor.getVideoInfo(url);
        Assert.notNull(videoInfo);
    }

    @Test
    public void testDownloadFormat() throws IOException, InterruptedException {
        String url = "https://www.bilibili.com/video/BV1U94y1q7hF?spm_id_from=333.1007.tianma.1-1-1.click";

        Config config = new Config();
        config.setBasePath("D:\\Temp\\yt-dlp");
        DefaultDownloadCallback callback = new DefaultDownloadCallback(webPageTaskMapper, webPageVideoTaskMapper);

        Downloader downloader = new YtDlpDownloaderNew(config, callback);
        downloader.download(url);
    }

}