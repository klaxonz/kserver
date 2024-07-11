package com.klaxon.kserver.module.spider.proxy;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GithubRepoProxyProvider {

    @Resource
    private OkHttpClient client;
    @Resource
    private ProxyCacheManager proxyCacheManager;

    public void fetch() {
        List<String> urls = Lists.newArrayList(
                "https://raw.githubusercontent.com/Zaeem20/FREE_PROXIES_LIST/master/http.txt",
                "https://raw.githubusercontent.com/Zaeem20/FREE_PROXIES_LIST/master/https.txt",
                "https://raw.githubusercontent.com/dunno10-a/proxy/main/proxies/http.txt",
                "https://raw.githubusercontent.com/elliottophellia/yakumo/master/results/http/global/http_checked.txt",
                "https://raw.githubusercontent.com/zloi-user/hideip.me/main/http.txt",
                "https://raw.githubusercontent.com/zloi-user/hideip.me/main/https.txt",
                "https://raw.githubusercontent.com/vakhov/fresh-proxy-list/master/https.txt",
                "https://raw.githubusercontent.com/proxifly/free-proxy-list/main/proxies/protocols/http/data.txt",
                "https://raw.githubusercontent.com/dunno10-a/proxy/main/proxies/http.txt",
                "https://raw.githubusercontent.com/MuRongPIG/Proxy-Master/main/http.txt",
                "https://raw.githubusercontent.com/yemixzy/proxy-list/main/proxies/http.txt",
                "https://raw.githubusercontent.com/saisuiu/Lionkings-Http-Proxys-Proxies/main/free.txt",
                "https://raw.githubusercontent.com/ProxyScraper/ProxyScraper/main/http.txt",
                "https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/http_proxies.txt",
                "https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/https_proxies.txt",
                "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt"
        );

        for (String url : urls) {
            fetchProxy(url);
        }
    }

    private void fetchProxy(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String proxyList = responseBody.string();
                    String[] proxyLines = proxyList.split("\n");
                    Pattern pattern = Pattern.compile("([0-9.]*)(/|:)([0-9]*)");
                    for (String proxy : proxyLines) {
                        Matcher matcher = pattern.matcher(proxy);
                        if (matcher.find()) {
                            String proxyMatch = matcher.group();
                            String[] proxySpilt = proxyMatch.split(":");
                            if (proxySpilt.length != 2 || StringUtils.isBlank(proxySpilt[0]) || StringUtils.isBlank(proxySpilt[1])) {
                                proxyCacheManager.removeProxy(proxy);
                            } else {
                                int port = Integer.parseInt(proxySpilt[1]);
                                if (port <= 0 || port > 65535) {
                                    proxyCacheManager.removeProxy(proxy);
                                    return;
                                }
                                proxyCacheManager.addProxy(proxyMatch);
                            }
                        }
                    }
                }
            } else {
               log.info("无法获取代理IP列表: {}", response.code());
            }
        } catch (IOException e) {
            log.info("发生异常: {}", e.getMessage());
        }
    }

}
