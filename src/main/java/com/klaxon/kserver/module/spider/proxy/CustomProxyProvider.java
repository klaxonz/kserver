package com.klaxon.kserver.module.spider.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class CustomProxyProvider implements ProxyProvider {

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final okhttp3.Request proxyRequest = new okhttp3.Request.Builder()
            .url("http://localhost:5010/get")
            .method("GET", null)
            .build();

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        if (!page.isDownloadSuccess()) {
            okhttp3.Request removeRequest = new okhttp3.Request.Builder()
                    .url("http://localhost:5010/delete?proxy=" + proxy.getHost() + ":" + proxy.getPort())
                    .method("GET", null)
                    .build();
            try (Response execute = client.newCall(removeRequest).execute();) {
                log.info("Remove proxy {}", proxy.getHost() + ":" + proxy.getPort());
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public Proxy getProxy(Request request, Task task) {
        try (Response execute = client.newCall(proxyRequest).execute();) {
            String response = execute.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {});
            String proxy = map.get("proxy");
            String[] proxySplits = proxy.split(":");
            String host = proxySplits[0];
            String port = proxySplits[1];
            log.info("Get proxy {}", proxy);
            return new Proxy(host, Integer.parseInt(port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
