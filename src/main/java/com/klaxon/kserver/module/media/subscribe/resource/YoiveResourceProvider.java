package com.klaxon.kserver.module.media.subscribe.resource;

import com.google.common.collect.Lists;
import com.klaxon.kserver.module.media.subscribe.task.SubscribeTask;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

@Component
public class YoiveResourceProvider implements ResourceProvider {

    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build();

    private Request buildRequest(String url) {
        Headers headers = Headers.of(
                "authority", "bbs.yiove.com",
                "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "accept-language", "zh-CN,zh;q=0.9,zh-Hans;q=0.8,en;q=0.7,zh-Hant;q=0.6,ja;q=0.5,und;q=0.4,de;q=0.3,fr;q=0.2,cy;q=0.1",
                "cache-control", "no-cache",
                "dnt", "1",
                "pragma", "no-cache",
                "user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        );
        return new Request.Builder()
                .url(url)
                .method("GET", null)
                .headers(headers)
                .build();
    }

    @Override
    public SubscribeTask.ExtractInfo parse(String name, Integer year) {

        try {
            String query = URLEncoder.encode(name, "UTF-8").replaceAll("%", "_");
            String url = String.format("https://bbs.yiove.com/search-%s-1.htm", query);

            Request request = buildRequest(url);
            Response response = client.newCall(request).execute();
            String html = response.body().string();
            Elements movieElements = Jsoup.parse(html).select("ul.threadlist > li");

            String shareUrl = null;
            String alipanShareUrl = null;
            for (Element element : movieElements) {
                String rootText = element.text();
                if (!StringUtils.contains(rootText, "阿里云")) {
                    continue;
                }
                if (!StringUtils.contains(rootText, String.format("(%s)", year))) {
                    continue;
                }
                Elements aElement = element.select("div > div.subject.break-all > a:nth-child(1)");
                String originTitle = aElement.text();
                String title = originTitle;
                String[] splits = title.split("\\(");
                if (splits.length == 0) {
                    splits = title.split(" ");
                    if (splits.length == 0) {
                        continue;
                    }
                }
                title = splits[0].trim();

                if (!StringUtils.equals(title, name) || !StringUtils.contains(originTitle, String.valueOf(year))) {
                    continue;
                }

                // 获取阿里云盘链接信息页面
                String href = aElement.attr("href");
                shareUrl = String.format("https://bbs.yiove.com/%s", href);
                request = buildRequest(shareUrl);

                response = client.newCall(request).execute();
                html = response.body().string();

                Elements aElements = Jsoup.parse(html).select(".markdown-body a");
                for (Element aEl : aElements) {
                    href = aEl.attr("href");
                    if (StringUtils.contains(href, "aliyundrive.com")) {
                        alipanShareUrl = URLDecoder.decode(href.replaceAll("_", "%"), "UTF-8");
                        break;
                    } else {
                        String jumpUrl = href;
                        if (StringUtils.contains(href, "gowild.htm")) {
                            jumpUrl = aElement.attr("href").replace("gowild.htm?url=", "");
                        }
                        jumpUrl = URLDecoder.decode(jumpUrl, "UTF-8").replaceAll("_", "%");;

                        // 云盘链接详情
                        request = buildRequest(jumpUrl);

                        response = client.newCall(request).execute();
                        html = response.body().string();
                        alipanShareUrl = Jsoup.parse(html).select("#main > p:nth-child(1) > a").attr("href");
                        break;
                    }
                }

                if (Objects.nonNull(alipanShareUrl)) {
                    break;
                }
            }

            if (Objects.nonNull(alipanShareUrl)) {
                return SubscribeTask.ExtractInfo.builder()
                        .name(name)
                        .resourceUrl(shareUrl)
                        .cloudDiskUrls(Lists.newArrayList(alipanShareUrl))
                        .build();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String getName() {
        return "Yiove";
    }
}
