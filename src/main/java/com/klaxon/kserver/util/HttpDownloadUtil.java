package com.klaxon.kserver.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpDownloadUtil {

    public static void downloadFile(Request request, File target) {

        // 获取CPU核心数
        int threadNum = 4;

        try {
            // 开启连接
            URL targetUrl = new URL(request.getUrl());
            URLConnection connection = targetUrl.openConnection();

            // 设置请求头
            if (!Objects.isNull(request.getHeaders())) {
                for (String key : request.getHeaders().keySet()) {
                    String value = request.getHeaders().get(key);
                    connection.setRequestProperty(key, value);
                }
            }

            // 计算线程数
            long total = connection.getContentLengthLong();
            long rangeSizePerThread = total / threadNum;

            // 创建线程池
            ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

            // 执行下载任务
            CountDownLatch latch = new CountDownLatch(threadNum);
            DownloadState downloadState = new DownloadState(latch);
            for (int i = 0; i < threadNum; i++) {
                long startRange = i * rangeSizePerThread;
                long endRange = startRange + rangeSizePerThread - 1;
                if (i == threadNum - 1) {
                    if (endRange < total - 1) {
                        endRange = total;
                    }
                }
                Request rangeRequest = new RequestBuilder().url(request.getUrl())
                        .headerPutAll(request.getHeaders())
                        .header("Range", "bytes=" + startRange + "-" + endRange)
                        .listener(request.getListener())
                        .build();
                DownloadFileTask downloadFileTask = new DownloadFileTask(rangeRequest, target, i, total, downloadState);
                executorService.execute(downloadFileTask);
            }

            // 等待线程下载完成
            latch.await();
            executorService.shutdown();

            // 合并文件
            File[] files = new File[threadNum];
            for (int i = 0; i < threadNum; i++) {
                String tempFilePath = FilePathUtil.getTempFilePath(target, i);
                files[i] = new File(tempFilePath);
            }
            FileUtil.mergeFile(target, files);

            // 删除临时文件
            Arrays.stream(files).forEach(File::delete);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
