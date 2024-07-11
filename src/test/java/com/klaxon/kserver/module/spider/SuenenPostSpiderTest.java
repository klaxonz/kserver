package com.klaxon.kserver.module.spider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@ActiveProfiles("dev")
class SuenenPostSpiderTest {

    @Resource
    private SuenenPostSpider suenenPostSpider;

    @Test
    void run() {
        suenenPostSpider.run();
    }
}