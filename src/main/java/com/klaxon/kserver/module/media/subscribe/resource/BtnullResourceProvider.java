package com.klaxon.kserver.module.media.subscribe.resource;

import cn.hutool.json.JSON;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class BtnullResourceProvider implements ResourceProvider {

    private String cookie = "PHPSESSID=kj7r7661atm8dcooo58qp3i1e5; BT_auth=6cc0nDAAsbMfRxZvUBiCyFk3O8VjiSEHJ8u3Yki3CRsYueHP-JrsMqgurWEYFYZJ5jOH_A6NjNno-swb4meD2QxmWSrU4B5XNcL-UqCsOQ8qwJqGxJuJjwRUUWRg1J_SiCj_-hBNUHWWpm_PfZjkniGvtOvqe9H_KunuPZEG8at9764; BT_cookietime=ea95t6QURopxSM-LLcKWnlYvoAYH3ilir8FimRZpT3gds-lT6MPu;vrg_go=1;";
    private String cookieWithCloudflare = "";

    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build();

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
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(data));
        return new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .headers(okhttpHeader)
                .build();
    }

    private Map<String, Object> buildRequestHeaders(boolean withCloudflare) {
        Map<String, Object> headers = Maps.newHashMap();
        if (withCloudflare && StringUtils.isNotBlank(cookieWithCloudflare)) {
            headers.put("cookie", cookieWithCloudflare);
        } else {
            headers.put("cookie", cookie);
        }
        return headers;
    }

    private void updateCookie(String resourceUrl) throws IOException {
        String url = String.format("http://127.0.0.1:8080/fetch?url=%s", resourceUrl);
        client.newCall(buildRequest(url, buildRequestHeaders(false), null)).execute();
    }


    @Override
    public SubscribeTask.ExtractInfo parse(String name, Integer year) {
       try {
           WebPageInfo pageInfo = findMovie(name);
           if (Objects.isNull(pageInfo) || StringUtils.contains(pageInfo.getBody(), "浏览器验证未通过")){
               updateCookie("https://www.btnull.net/");
               pageInfo = findMovie(name);
           }
           if (Objects.isNull(pageInfo) || !StringUtils.contains(pageInfo.getBody(), "html")) {
               return null;
           }

           Elements elements = Jsoup.parse(pageInfo.getBody()).select(".sr_lists > div");
           for (Element element : elements) {
               String text = element.select(".text a").text();
               String title = text.split(" ")[0].trim();
               title = title.split("\\(")[0];
               if (!StringUtils.equals(title, name)) {
                   continue;
               }
               if (!StringUtils.contains(text, String.valueOf(year))) {
                   continue;
               }
               String resourceUrl = "https://www.btnull.net" + element.select(".text a").attr("href");
               updateCookie(resourceUrl);
               String detailUrl = "https://www.btnull.net/ajax/downurl/" + element.select(".text a").attr("href")
                       .replace("/mv/", "").replace(".html", "") + "_mv/";
               WebPageInfo movieDetail = findMovieDetail(resourceUrl, detailUrl);

               if (Objects.isNull(movieDetail)) {
                   continue;
               }

               JSON jsonObj = JSONUtil.parse(movieDetail.getBody());
               List<String> tnames = (List<String>) JSONUtil.getByPath(jsonObj, "$.panlist.tname");
               List<Integer> types = (List<Integer>) JSONUtil.getByPath(jsonObj, "$.panlist.type");
               List<String> urls = (List<String>) JSONUtil.getByPath(jsonObj, "$.panlist.url");
               if (Objects.isNull(tnames) || tnames.size() == 0) {
                   continue;
               }
               Integer index = null;
               for (int i = 0; i < tnames.size(); i++) {
                   if (StringUtils.equals(tnames.get(i), "阿里网盘")) {
                       index = i;
                       break;
                   }
               }
               if (Objects.isNull(index)) {
                   continue;
               }

               List<String> cloudDiskUrls = Lists.newArrayList();
               for (int i = 0; i < types.size(); i++) {
                   if (!Objects.equals(types.get(i), index)) {
                       continue;
                   }
                   cloudDiskUrls.add(urls.get(i));
               }

                return SubscribeTask.ExtractInfo.builder()
                        .name(name)
                        .cloudDiskUrls(cloudDiskUrls)
                        .resourceUrl(resourceUrl)
                        .build();
           }
       } catch (IOException e) {
           throw new RuntimeException(e);
       }

        return null;
    }

    @Override
    public String getName() {
        return "BtNull";
    }

    private WebPageInfo findMovie(String name) throws IOException {
        String param = String.format("https://www.btnull.net/s/1-1--1/%s", URLEncoder.encode(name, "UTF-8"));
        String url = String.format("http://127.0.0.1:8080/fetch?url=%s", param);

        Map<String, Object> headers = buildRequestHeaders(true);

        Response response = client.newCall(buildRequest(url, headers, null)).execute();
        String html = response.body().string();
        if (!response.isSuccessful()) {
            return null;
        }
        return JSONUtil.toBean(html, WebPageInfo.class);
    }

    private WebPageInfo findMovieDetail(String resourceUrl, String detailUrl) throws IOException {
        String url = String.format("http://127.0.0.1:8080/fetch?url=%s", detailUrl);
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("referer", resourceUrl);
        Response response = client.newCall(buildRequest(url, headers, null)).execute();
        String responseStr = response.body().string();
        if (!response.isSuccessful()) {
            return null;
        }
        WebPageInfo webPageInfo = JSONUtil.toBean(responseStr, WebPageInfo.class);
        if (Objects.isNull(webPageInfo) || !StringUtils.isNotBlank(webPageInfo.getBody())) {
            return null;
        }
        return webPageInfo;
    }

}
