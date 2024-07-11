package com.klaxon.kserver.module.spider;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;

@Component
public class WpzysqPostSpider {

    @Resource
    private WpzysqPostPageModelPipeline wpzysqPostPageModelPipeline;

    @SneakyThrows
    public void run() {
        Spider spider = OOSpider.create(Site.me().setDomain("app.wpzysq.net").setCycleRetryTimes(5), wpzysqPostPageModelPipeline, WpzysqPost.class)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .startUrls(Lists.newArrayList(
                        "https://app.wpzysq.net/forum-1-1.htm?tagids=108_0_0_0"
                ))
                .thread(10);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
