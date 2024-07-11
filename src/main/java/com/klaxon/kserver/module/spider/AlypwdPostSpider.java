package com.klaxon.kserver.module.spider;

import com.google.common.collect.Lists;
import com.klaxon.kserver.module.spider.proxy.CustomProxyProvider;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AlypwdPostSpider {

    @Resource
    private AlypwdPostPageModelPipeline alypwdPostPageModelPipeline;

    @SneakyThrows
    public void run() {
        Spider spider = OOSpider.create(Site.me().setDomain("www.alypw.com").setCycleRetryTimes(5), alypwdPostPageModelPipeline, AlypwdPost.class)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .startUrls(Lists.newArrayList(
                        "https://www.alypw.com/category-2.html",
                        "https://www.alypw.com/category-15.html",
                        "https://www.alypw.com/tags-2.html",
                        "https://www.alypw.com/category-38.html"
                ))
                .thread(10);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
