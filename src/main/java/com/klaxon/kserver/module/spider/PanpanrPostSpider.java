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
public class PanpanrPostSpider {

    @Resource
    private PanpanrPostPageModelPipeline panpanrPostPageModelPipeline;

    @SneakyThrows
    public void run() {
        Spider spider = OOSpider.create(Site.me().setDomain("www.panpanr.top").setCycleRetryTimes(5), panpanrPostPageModelPipeline, PanpanrPost.class)
                .setScheduler(new RedisScheduler("localhost").setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .startUrls(Lists.newArrayList(
                        "https://www.panpanr.top/forum.php?mod=forumdisplay&fid=2&filter=typeid&typeid=1",
                        "https://www.panpanr.top/forum.php?mod=forumdisplay&fid=2&filter=typeid&typeid=2",
                        "https://www.panpanr.top/forum.php?mod=forumdisplay&fid=2&filter=typeid&typeid=3"
                ))
                .thread(10);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
