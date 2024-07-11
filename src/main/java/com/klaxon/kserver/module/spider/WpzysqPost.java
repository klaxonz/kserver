package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://app.wpzysq.net/thread-*")
@HelpUrl(value = {
        "https://app.wpzysq.net/forum-1-\\d+.htm\\?tagids=108_0_0_0",
})
@Data
public class WpzysqPost extends SimpleModel implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
