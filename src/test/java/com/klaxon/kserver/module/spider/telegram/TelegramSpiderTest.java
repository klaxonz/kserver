package com.klaxon.kserver.module.spider.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class TelegramSpiderTest {

    @Resource
    private TelegramSpider telegramSpider;

    @Test
    void run() {
        try {
            telegramSpider.run(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}