package com.klaxon.kserver.module.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
@ActiveProfiles("dev")
class PrimePanPostSpiderTest {

    @Resource
    private PrimePanPostSpider primePanPostSpider;

    @Test
    void run() {
        primePanPostSpider.run();
    }
}