package com.klaxon.kserver.module.spider.task;


import com.klaxon.kserver.module.spider.proxy.GithubRepoProxyProvider;
import com.klaxon.kserver.module.spider.proxy.ProxyCacheManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
public class ProxyTask {


    @Resource
    private ProxyCacheManager proxyCacheManager;
    @Resource
    private GithubRepoProxyProvider proxyProvider;

    // @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void test() {
        log.info("Start run task to test proxy ip");
        Set<String> proxies = proxyCacheManager.getProxiesByScoreRange(1, 100);
        for (String proxy : proxies) {
        }
        log.info("End run task to test proxy ip");

    }

    // @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void fetch() {
        log.info("Start run task to fetch proxy");
        proxyProvider.fetch();
        log.info("End run task to fetch proxy");
    }
}
