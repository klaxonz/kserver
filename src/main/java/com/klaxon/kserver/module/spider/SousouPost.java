package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://a.sousou.pro/thread-*")
@HelpUrl(value = {
        "https://a.sousou.pro/forum-4-\\d+.htm\\?tagids=175_0_0_0",
        "https://a.sousou.pro/forum-2-\\d+.htm\\?tagids=167_0_0_0",
        "https://a.sousou.pro/forum-3-\\d+.htm\\?tagids=171_0_0_0"
})
@Data
public class SousouPost extends SimpleModel implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
