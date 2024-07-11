package com.klaxon.kserver.module.media.subscribe.resource;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.klaxon.kserver.module.media.subscribe.task.SubscribeTask;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RrdyResourceProvider implements ResourceProvider {

    @Resource
    private OkHttpClient client;

    private Request buildRequest(String url, Map<String, Object> headers, Map<String, Object> body) {
        Headers okhttpHeader = Headers.of(
                "cache-control", "no-cache",
                "dnt", "1",
                "pragma", "no-cache",
                "user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        );

        Map<String, Object> data = Maps.newHashMap();
        data.put("headers", headers);
        data.put("body", body);
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"),JSONUtil.toJsonStr(data));
        return new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .headers(okhttpHeader)
                .build();
    }

    private Map<String, Object> buildRequestHeaders() {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("host", "www.rrdynb.com");
        headers.put("cache-control", "no-cache");
        headers.put("dnt", "1");
        headers.put("pragma", "no-cache");
        headers.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.put("cookie", "PHPSESSID=pc9g090t908l92d8hlt0458661; cf_clearance=ts6yeVP7E.Od9T4YtgvgLh03Pg8qzsB7hgCjMQbXfXo-1712141616-1.0.1.1-XuEdhbxgdfG2dPEEUMoXpnRkwT5bjg59UgAAafZ9gvBQsWv2eBfIUe2kFBnZveyIQ.AGJenR3796orX2HICiWw");
        return headers;
   }


    @Override
    public SubscribeTask.ExtractInfo parse(String name, Integer year) {
        try {
            String param = String.format("https://www.rrdynb.com/plus/search.php?q=%s&pagesize=10&submit=", URLEncoder.encode(name, "UTF-8"));
            String url = String.format("http://127.0.0.1:8080/fetch?url=%s", param);

            Map<String, Object> headers = buildRequestHeaders();

            Response response = client.newCall(buildRequest(url, headers, null)).execute();
            String html = response.body().string();
            if (!response.isSuccessful()) {
                return null;
            }
            WebPageInfo webPageInfo = JSONUtil.toBean(html, WebPageInfo.class);
            if (Objects.isNull(webPageInfo) || !StringUtils.contains(webPageInfo.getBody(), "html")) {
                return null;
            }

            Elements elements = Jsoup.parse(webPageInfo.getBody()).select("#movielist .intro a");
            for (Element element : elements) {
                String text = element.text();
                String patternStr = "《(.*?)》";

                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(text);

                if (!matcher.find()) {
                    continue;
                }

                String itemTitle = matcher.group(1).trim();
                if (!StringUtils.equals(itemTitle, name)) {
                    continue;
                }
                if (!StringUtils.contains(text, "阿里云盘")) {
                    continue;
                }

                // 获取详情
                String detailUrl = "https://www.rrdynb.com/" + element.attr("href");
                url = String.format("http://127.0.0.1:8080/fetch?url=%s", detailUrl);
                response = client.newCall(buildRequest(url, headers, null)).execute();

                html = response.body().string();
                if (!response.isSuccessful()) {
                    continue;
                }
                webPageInfo = JSONUtil.toBean(html, WebPageInfo.class);
                if (Objects.isNull(webPageInfo) || !StringUtils.contains(webPageInfo.getBody(), "html")) {
                    continue;
                }
                Document doc = Jsoup.parse(webPageInfo.getBody());
                Element shareElement = doc.selectFirst("a[href*=https://www.alipan.com]");
                if (Objects.isNull(shareElement)) {
                    continue;
                }
                String shareUrl = shareElement.attr("href");
                if (StringUtils.isBlank(shareUrl)) {
                    continue;
                }

                // match release year
                patternStr = "(\\d{4})-(\\d{2})-(\\d{2})";
                pattern = Pattern.compile(patternStr);
                matcher = pattern.matcher(html);

                if (!matcher.find()) {
                   continue;
                }

                patternStr = "(\\d{4})-(\\d{2})-(\\d{2})";
                pattern = Pattern.compile(patternStr);
                matcher = pattern.matcher(html);

                String yearStr = null;
                int matchCount = 0;
                while (matcher.find() && matchCount < 3) { // 修改条件，查找至第三个匹配项
                    matchCount++;
                    if (matchCount == 3) {
                        yearStr = matcher.group(1);
                        break; // 找到所需结果后跳出循环
                    }
                }

                if (StringUtils.isBlank(yearStr) && !StringUtils.equals(yearStr, String.valueOf(year))) {
                    continue;
                }

                return SubscribeTask.ExtractInfo.builder()
                        .resourceUrl(detailUrl)
                        .cloudDiskUrls(Lists.newArrayList(shareUrl))
                        .name(name)
                        .build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getName() {
        return "Rrdy";
    }
}
