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
public class AliyunpanPostSpider {

    @Resource
    private AliyunpanPostPageModelPipeline aliyunpanPostPageModelPipeline;

    @SneakyThrows
    public void run() {
        Spider spider = OOSpider.create(Site.me().setDomain("aliyunpan1.com").setCycleRetryTimes(5), aliyunpanPostPageModelPipeline, AliyunpanPost.class)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .startUrls(Lists.newArrayList(
                        "https://aliyunpan1.com/thread.php?fid-2.html"
                ))
                .thread(10);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
