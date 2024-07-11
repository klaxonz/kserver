package com.klaxon.kserver.module.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
@ActiveProfiles("dev")
class AliyunpanPostSpiderTest {

    @Resource
    private AliyunpanPostSpider aliyunpanPostSpider;

    @Test
    void run() {
        aliyunpanPostSpider.run();
    }
}