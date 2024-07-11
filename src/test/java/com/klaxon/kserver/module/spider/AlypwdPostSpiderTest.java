package com.klaxon.kserver.module.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class AlypwdPostSpiderTest {

    @Resource
    private AlypwdPostSpider alypwdPostSpider;

    @Test
    void run() {
        alypwdPostSpider.run();
    }
}