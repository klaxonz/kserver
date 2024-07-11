package com.klaxon.kserver.module.spider;

import lombok.Data;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("https://my.oschina.net/*/blog/\\d+")
@HelpUrl(value = {
        "https://my.oschina.net/u/\\d+",
        "https://www.oschina.net/news/\\d+",
        "https://www.oschina.net/blog/*",
        "https://www.oschina.net/group/*",
})
@Data
public class OschinaBlog implements AfterExtractor {

    @ExtractBy(value = "//*[@id=\"mainScreen\"]/div/div[1]/div[1]/div[2]/div[1]/div/div[1]/div/div[1]/h1/a/@href", notNull = true)
    private String url;

    @ExtractBy(value = "//*[@id=\"mainScreen\"]/div/div[1]/div[1]/div[2]/div[1]/div/div[1]/div/div[1]/h1/a/text()")
    private String title;

    @Override
    public void afterProcess(Page page) {
    }
}
