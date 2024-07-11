package com.klaxon.kserver.module.spider.task;

import com.klaxon.kserver.KServerApplication;
import com.klaxon.kserver.config.SchedulerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;


@ActiveProfiles("dev")
@SpringBootTest(
    classes = {KServerApplication.class, SchedulerConfig.class},

    properties = {
        "scheduling.enable: false"
    }
)
public class DriveShareScrapeTaskTest {

    @Resource
    private DriveShareScrapeTask driveShareScrapeTask;

    @Test
    public void scrape() {
        driveShareScrapeTask.scrape();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

}