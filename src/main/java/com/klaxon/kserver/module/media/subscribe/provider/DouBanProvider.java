package com.klaxon.kserver.module.media.subscribe.provider;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DouBanProvider implements MetaProvider {

    @Override
    public Boolean isSupport() {
        return false;
    }

    @Override
    public List<MovieItem> parse() {
        String url = "https://movie.douban.com/cinema/nowplaying/guangzhou/";

        List<MovieItem> movieItems = Lists.newArrayList();

        // 使用Jsoup连接到网页
        try {
            Document doc = Jsoup.connect(url).get();

            // 根据网页结构选择需要的数据
            // 假设我们需要获取的是即将上映的电影列表，我们可能需要找到包含这些信息的元素
            Elements upcomingMovies = doc.select("div#nowplaying .lists > li"); // 这里的选择器需要根据实际网页结构进行调整

            // 遍历电影列表
            for (Element movie : upcomingMovies) {
                // 获取电影名称
                String title = movie.attr("data-title");
                // 获取电影上映日期
                String releaseDate = movie.attr("data-release");

                MovieItem movieItem = new MovieItem();
                movieItem.setTitle(title);
                movieItem.setRelease(Integer.valueOf(releaseDate));
                movieItem.setType(1);

                movieItems.add(movieItem);
            }
        } catch (Exception e) {
            log.warn("Parse douban movie failed", e);
        }

        return movieItems;
    }

}
