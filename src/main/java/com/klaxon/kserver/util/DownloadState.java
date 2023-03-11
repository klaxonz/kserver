package com.klaxon.kserver.util;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class DownloadState {

    private final CountDownLatch latch;
    private final AtomicLong readSum;
    private final AtomicLong lastSecondSpeed;
    private final AtomicLong lastSecondProgress;
    private final AtomicLong lastTime;


    public DownloadState(CountDownLatch latch) {
        this.latch = latch;
        this.readSum = new AtomicLong(0);
        this.lastSecondSpeed = new AtomicLong(0);
        this.lastSecondProgress = new AtomicLong(0);
        this.lastTime = new AtomicLong(0);
    }

}
