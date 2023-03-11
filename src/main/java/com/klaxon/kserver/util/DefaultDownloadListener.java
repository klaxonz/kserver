package com.klaxon.kserver.util;

import cn.hutool.core.io.unit.DataSizeUtil;

import java.io.File;

public class DefaultDownloadListener implements DownloadListener {

    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(long total, long progress, long speed) {
        System.out.printf("\r当前: %8s, 总共%8s, 下载速度：%8s/s", DataSizeUtil.format(progress), DataSizeUtil.format(total), DataSizeUtil.format(speed));
    }

    @Override
    public void onSpeedChange(long speed) {

    }

    @Override
    public void onFinished(File file) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
