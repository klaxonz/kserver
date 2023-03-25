package com.klaxon.kserver.downloader;

import com.jfposton.ytdlp.*;

public class DownloadTask implements Runnable{

    private final YtDlpRequest request;

    public DownloadTask(YtDlpRequest request) {
        this.request = request;
    }

    @Override
    public void run() {
        YtDlpResponse response = null;
        try {
            response = YtDlp.execute(request, (progress, etaInSeconds) ->{
                System.out.println(progress + "%");
            });
        } catch (YtDlpException e) {
            e.printStackTrace();
        }
    }
}
