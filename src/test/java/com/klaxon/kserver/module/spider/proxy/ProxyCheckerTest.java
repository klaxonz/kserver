package com.klaxon.kserver.module.spider.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;


@SpringBootTest
@ActiveProfiles("dev")
class ProxyCheckerTest {

    @Resource
    private ProxyChecker proxyChecker;

    @Test
    void check() {
    }

}