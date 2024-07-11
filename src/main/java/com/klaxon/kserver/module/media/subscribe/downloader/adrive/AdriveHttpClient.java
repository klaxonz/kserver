package com.klaxon.kserver.module.media.subscribe.downloader.adrive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.module.spider.proxy.ProxyCacheManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Component
public class AdriveHttpClient {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private TokenManager tokenManager;
    @Resource
    private ProxyCacheManager proxyCacheManager;

    private Request.Builder createRequestBuilder(String url, String shareToken) {
        Request.Builder builder = new Request.Builder().url(url)
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,zh-Hans;q=0.8,en;q=0.7,zh-Hant;q=0.6,ja;q=0.5,und;q=0.4,de;q=0.3,fr;q=0.2,cy;q=0.1")
                .addHeader("cache-control", "no-cache")
                .addHeader("content-type", "application/json")
                .addHeader("origin", "https://www.aliyundrive.com")
                .addHeader("pragma", "no-cache")
                .addHeader("referer", "https://www.aliyundrive.com/")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .addHeader("x-requested-with", "XMLHttpRequest");

        if (StringUtils.isNotBlank(shareToken)) {
                builder.addHeader("authorization", "Bearer " + tokenManager.getAccessToken());
        }
        if (StringUtils.isNotBlank(shareToken)) {
            builder.addHeader("x-share-token", shareToken);
        }
        return builder;
    }

    public String get(String url) {
        Request.Builder builder = createRequestBuilder(url, null);
        Request request = builder.build();

        try {
            OkHttpClient client = newHttpClient();
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
        } catch (IOException e) {
            log.info("Http request failed, {}", url, e);
        }
        return null;
    }


    public Map<String, Object> post(String url, Object body) {
        return post(url, body, null);
    }


    public Map<String, Object> post(String url, Object body, Map<String, Object> headers) {
        Request.Builder builder = createRequestBuilder(url, null);

        RequestBody requestBody = null;
        if (body != null) {
            try {
                String bodyJson = objectMapper.writeValueAsString(body);
                requestBody = RequestBody.create(MediaType.get("application/json"), bodyJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header).toString());
            }
        }

        try {
            builder.post(requestBody);
            Request request = builder.build();
            OkHttpClient client = newHttpClient();
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                if (body != null) {
                    return objectMapper.readValue(responseBodyString, new TypeReference<Map<String, Object>>() {});
                }
            }
        } catch (IOException e) {
            log.info("Http request failed, {}, message: {}", url, e.getMessage());
        }
        return null;
    }

    public OkHttpClient newHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // String proxyOrigin = proxyCacheManager.getRandomProxy();
        // log.info("using proxy: {}", proxyOrigin);
        // if (StringUtils.isNotBlank(proxyOrigin)) {
        //     String ip = proxyOrigin.split(":")[0];
        //     int port = Integer.parseInt(proxyOrigin.split(":")[1]);
        //     Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        //     builder.proxy(proxy);
        // }
        builder.connectTimeout(Duration.of(15000, ChronoUnit.SECONDS))
                .callTimeout(Duration.of(15000, ChronoUnit.SECONDS))
                .readTimeout(Duration.of(15000, ChronoUnit.SECONDS))
                .writeTimeout(Duration.of(15000, ChronoUnit.SECONDS));
        return builder.build();
    }



}
