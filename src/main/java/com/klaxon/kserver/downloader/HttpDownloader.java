package com.klaxon.kserver.downloader;

import okhttp3.OkHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class HttpDownloader extends AbstractDownloader {

    @Override
    void download(String url) {

    }

    @Override
    public void download(String url, File destination) throws IOException {
        URL urlSources = new URL(url);
        BufferedInputStream bis = new BufferedInputStream(urlSources.openStream());
        FileOutputStream fis = new FileOutputStream(destination);
        byte[] buffer = new byte[4096];
        int count=0;
        while ((count = bis.read(buffer,0,1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
}
