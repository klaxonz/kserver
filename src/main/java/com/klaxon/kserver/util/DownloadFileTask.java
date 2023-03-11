package com.klaxon.kserver.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileTask implements Runnable{

    private final File targetFile;

    private final Request request;

    private final Integer index;

    private final Long total;

    private final DownloadListener listener;

    private final DownloadState downloadState;

    public DownloadFileTask(Request request, File targetFile,
                            Integer index, Long total, DownloadState downloadState) {
        this.index = index;
        this.total = total;
        this.request = request;
        this.targetFile = targetFile;
        this.downloadState = downloadState;
        this.listener = request.getListener();
    }

    @Override
    public void run() {
        String tempFilePath = FilePathUtil.getTempFilePath(targetFile, index);
        File tempFile = new File(tempFilePath);

        // 打开连接
        URLConnection urlConnection;
        try {
            urlConnection = new URL(request.getUrl()).openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 设置请求头
        for (String key : request.getHeaders().keySet()) {
            String value = request.getHeaders().get(key);
            urlConnection.setRequestProperty(key, value);
        }
        RandomAccessFile randomAccessFile;
        long length;
        try {
            randomAccessFile = new RandomAccessFile(tempFile, "rw");
            length = tempFile.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 恢复下载进度
        downloadState.getReadSum().getAndAdd(length);

        int contentLength = urlConnection.getContentLength();
        if (length < contentLength) {
            // 说明还没有下载完成
            try {
                urlConnection = new URL(request.getUrl()).openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String startRange = request.getHeaders().get("Range").split("=")[1].split("-")[0];
            String endRange = request.getHeaders().get("Range").split("=")[1].split("-")[1];
            request.getHeaders().put("Range", "bytes=" + (Long.parseLong(startRange) + length) + "-" + endRange);

            // 设置请求头
            for (String key : request.getHeaders().keySet()) {
                String value = request.getHeaders().get(key);
                urlConnection.setRequestProperty(key, value);
            }

            byte[] buffer = new byte[16 * 1024];
            try (InputStream inputStream = urlConnection.getInputStream()) {
                if (listener != null) {
                    listener.onStart();
                }
                randomAccessFile.seek(length);
                int readLength;
                while ((readLength = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, readLength);
                    synchronized (downloadState) {
                        downloadState.getReadSum().getAndAdd(readLength);
                        if (listener != null) {
                            if (System.currentTimeMillis() - downloadState.getLastTime().longValue() > 100) {
                                downloadState.getLastTime().set(System.currentTimeMillis());
                                downloadState.getLastSecondSpeed().set(downloadState.getReadSum().longValue() - downloadState.getLastSecondProgress().longValue());
                                downloadState.getLastSecondProgress().set(downloadState.getReadSum().longValue());
                                listener.onProgress(total, downloadState.getReadSum().longValue(), downloadState.getLastSecondSpeed().longValue() * 10);
                            } else {
                                listener.onProgress(total, downloadState.getReadSum().longValue(), downloadState.getLastSecondSpeed().longValue() * 10);
                            }
                        }
                    }
                }
                if (listener != null) {
                    listener.onFinished(tempFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        downloadState.getLatch().countDown();
    }




}
