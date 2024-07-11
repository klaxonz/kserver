package com.klaxon.kserver.module.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
@ActiveProfiles("dev")
class RrdynbPostSpiderTest {

    @Resource
    private RrdynbPostSpider rrdynbPostSpider;

    @Test
    void run() {
        rrdynbPostSpider.run();
    }
}