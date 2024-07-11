package com.klaxon.kserver.module.spider.proxy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ProxyExample {


    public static void main(String[] args) throws Exception {
        String proxyHost = "35.185.196.38";
        int proxyPort = 3128;

        // 创建 OkHttpClient 实例并设置代理
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .build();

        // 创建一个请求
        String targetUrl = "https://www.aliyundrive.com/s/MF13TKVEJBg";
        Request request = new Request.Builder()
                .url(targetUrl)
                .build();

        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();

            // 打印响应码和响应体
            System.out.println("Response Code: " + response.code());
            System.out.println(response.body().string());

            // 关闭响应体
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
