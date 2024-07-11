package com.klaxon.kserver.module.media.subscribe.provider;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class BdysProvider implements MetaProvider {

    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build();

    @Override
    public Boolean isSupport() {
        return true;
    }

    @Override
    public List<MovieItem> parse() {
        String url = "https://www.yjys.me/s/all?type=0";
        Request request = buildRequest(url);

        List<MovieItem> movieItems = Lists.newArrayList();
        try (Response response = client.newCall(request).execute()) {
            String html = response.body().string();
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".card-link");
            for (Element element : elements) {
                url = "https://www.yjys.me" + element.select("a").attr("href").trim().split(";")[0];
                String title = element.select(".text-truncate").text().trim();
                Integer year = extractMovieReleaseYear(url);
                if (Objects.isNull(year)) {
                    log.info("cant not extract release year, movie name: {}", title);
                    continue;
                }

                MovieItem movieItem = new MovieItem();
                movieItem.setTitle(title);
                movieItem.setRelease(year);
                movieItems.add(movieItem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return movieItems;
    }

    private Request buildRequest(String url) {
        Headers headers = Headers.of(
                "authority", "www.bdys10.com",
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

    public Integer extractMovieReleaseYear(String url) throws IOException {
        Request request = buildRequest(url);
        Response response = client.newCall(request).execute();
        String html = response.body().string();
        Document doc = Jsoup.parse(html);
        Element element = doc.selectFirst(".card-body div.row > div.col> p:nth-child(8)");
        if (Objects.isNull(element)) {
            return  null;
        }

        String pattern = "(\\d{4})-(\\d{2})-(\\d{2})";

        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(html);

        if (!matcher.find()) {
           return null;
        }

        String yearStr = matcher.group(1);
        return Integer.parseInt(yearStr);
    }


}
