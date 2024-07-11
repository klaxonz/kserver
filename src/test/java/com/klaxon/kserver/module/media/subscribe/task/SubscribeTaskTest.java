package com.klaxon.kserver.module.media.subscribe.task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;


@SpringBootTest
@ActiveProfiles("dev")
public class SubscribeTaskTest {

    @Resource
    private SubscribeTask subscribeTask;

    @Test
    public void testSubscribe() {
        // Call the method to test
        subscribeTask.subscribe();
    }

}