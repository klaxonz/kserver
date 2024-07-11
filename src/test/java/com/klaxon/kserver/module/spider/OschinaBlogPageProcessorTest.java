package com.klaxon.kserver.module.spider;

import com.klaxon.kserver.module.spider.proxy.CustomProxyProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;
import javax.management.JMException;

@SpringBootTest
@ActiveProfiles("dev")
public class OschinaBlogPageProcessorTest {

    @Resource
    private OschinaBlogPageModelPipeline oschinaBlogPageModelPipeline;

    @Test
    public void testSubscribe() throws JMException {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(new CustomProxyProvider());
        Spider spider = OOSpider.create(Site.me().setCycleRetryTimes(10), oschinaBlogPageModelPipeline, OschinaBlog.class)
                .setDownloader(httpClientDownloader)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .addUrl("https://www.oschina.net")
                .thread(40);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
