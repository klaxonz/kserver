package com.klaxon.kserver.downloader;

public interface RequestCallback {

    void onFinished();

    void progress(int current, int total);

    void onException();

}
