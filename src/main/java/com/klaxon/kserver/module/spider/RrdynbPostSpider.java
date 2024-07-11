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
public class RrdynbPostSpider {

    @Resource
    private RrdynbPostPageModelPipeline rrdynbPostPageModelPipeline;

    @SneakyThrows
    public void run() {
        Spider spider = OOSpider.create(Site.me().setDomain("www.rrdynb.com").setCycleRetryTimes(5), rrdynbPostPageModelPipeline, RrdynbPost.class)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .startUrls(Lists.newArrayList(
                        "https://www.rrdynb.com/movie/",
                        "https://www.rrdynb.com/dianshiju/",
                        "https://www.rrdynb.com/zongyi/",
                        "https://www.rrdynb.com/dongman/"
                ))
                .thread(10);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
