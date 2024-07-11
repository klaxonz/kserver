package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://suenen.com/thread-*")
@HelpUrl(value = {
        "https://suenen.com/forum-3-\\d+.htm\\?tagids=56_0_0_0",
})
@Data
public class SuenenPost extends SimpleModel implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
