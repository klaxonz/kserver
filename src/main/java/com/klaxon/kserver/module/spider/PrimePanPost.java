package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://pan.prime541.com/article2/*")
@HelpUrl(value = {
        "https://pan.prime541.com/posts2/*",
})
@Data
public class PrimePanPost extends SimpleModel implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
