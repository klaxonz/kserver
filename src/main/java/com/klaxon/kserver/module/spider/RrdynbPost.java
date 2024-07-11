package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl(value = {
        "https://www.rrdynb.com/movie/*",
        "https://www.rrdynb.com/dianshiju/*",
        "https://www.rrdynb.com/zongyi/*",
        "https://www.rrdynb.com/dongman/*"
})
@HelpUrl(value = {
        "https://www.rrdynb.com/movie/list_2_*",
        "https://www.rrdynb.com/dianshiju/list_6_*",
        "https://www.rrdynb.com/zongyi/list_10_*",
        "https://www.rrdynb.com/dongman/list_13_*"
})
@Data
public class RrdynbPost extends SimpleModel implements AfterExtractor {


    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
