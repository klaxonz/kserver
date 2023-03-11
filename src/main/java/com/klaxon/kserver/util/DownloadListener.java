package com.klaxon.kserver.util;

import java.io.File;

public interface DownloadListener {

    void onStart();

    void onProgress(long total, long progress, long speed);

    void onSpeedChange(long speed);

    void onFinished(File file);

    void onError(Throwable throwable);

}
