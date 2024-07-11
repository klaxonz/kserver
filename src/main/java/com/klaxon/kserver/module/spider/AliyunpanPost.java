package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://aliyunpan1.com/read.php\\?tid*")
@HelpUrl(value = {
        "https://aliyunpan1.com/thread.php\\?fid-2*",
})
@Data
public class AliyunpanPost extends SimpleModel implements AfterExtractor {


    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
