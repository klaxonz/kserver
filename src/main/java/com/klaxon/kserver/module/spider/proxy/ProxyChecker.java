package com.klaxon.kserver.module.spider.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProxyChecker {

    private static final int THREAD_POOL_SIZE = 700;

    private final String INFO_URL = "https://api.ipify.org";
    private final String GITHUB_URL = "https://github.com";
    private final String BAIDU_URL = "https://www.alipan.com";

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ProxyCacheManager proxyCacheManager;

    public void check() {
        Set<String> proxies = proxyCacheManager.getProxiesByScoreRange(1, 100);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for (String proxy : proxies) {
            executor.execute(() -> validateProxy(proxy));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void validateProxy(String proxy) {
        String[] proxySpilt = proxy.split(":");
        if (proxySpilt.length != 2 || StringUtils.isBlank(proxySpilt[0]) || StringUtils.isBlank(proxySpilt[1])) {
            proxyCacheManager.removeProxy(proxy);
            return;
        }
        String ip = proxySpilt[0];
        int port = Integer.parseInt(proxySpilt[1]);
        if (port <= 0 || port > 65535) {
            proxyCacheManager.removeProxy(proxy);
            return;
        }

        boolean isInfoValid = validate(ip, port, INFO_URL);
        boolean isGithubValid = validate(ip, port, GITHUB_URL);
        boolean isBaiduValid = validate(ip, port, BAIDU_URL);
        if (isInfoValid && (isGithubValid || isBaiduValid)) {
            proxyCacheManager.incrScore(proxy);
            proxyCacheManager.setFailed(proxy, 0);

            String country = proxyCacheManager.getCountry(proxy);
            if (StringUtils.isBlank(country)) {
                country = info(ip, port);
                if (StringUtils.isNotBlank(country)) {
                    proxyCacheManager.setCountry(proxy, country);
                }
            }
            if (isBaiduValid) {
                proxyCacheManager.setHighProxy(proxy);
            }
        } else {
            proxyCacheManager.decrScore(proxy);
            proxyCacheManager.setFailed(proxy);
        }

        int failed = proxyCacheManager.getFailed(proxy);
        if (failed >= 5) {
            proxyCacheManager.setScore(proxy, 0);
            proxyCacheManager.removeFailed(proxy);
            proxyCacheManager.removeCountry(proxy);
            proxyCacheManager.removeHighProxy(proxy);
        }
    }

    private boolean validate(String ip, int port, String testUrl) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            in.close();

            int responseCode = connection.getResponseCode();
            if (Objects.equals(responseCode, 200)) {
                return true;
            }
        } catch (IOException | NoSuchElementException e) {
            log.info("无法连接到代理服务器 {}:{}: {}", ip, port, e.getMessage());
        }

        return false;
    }

    private String info(String ip, int port) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            URL url = new URL("https://api.ip.sb/geoip");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseStr = response.toString().trim();
            Map<String, Object> proxyInfo = objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});

            return (String) proxyInfo.get("country_code");
        } catch (IOException | NoSuchElementException e) {
            log.info("无法获取ip信息 {}:{}: {}", ip, port, e.getMessage());
        }
        return null;
    }

}
