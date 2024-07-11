package com.klaxon.kserver.module.spider;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@EqualsAndHashCode(callSuper = true)
@TargetUrl("https://www.panpanr.top/thread-*")
@HelpUrl(value = {
        "https://www.panpanr.top/forum.php\\?mod=forumdisplay&fid=2&filter=typeid&typeid=1&page=\\d+",
        "https://www.panpanr.top/forum.php\\?mod=forumdisplay&fid=2&filter=typeid&typeid=2&page=\\d+",
        "https://www.panpanr.top/forum.php\\?mod=forumdisplay&fid=2&filter=typeid&typeid=3&page=\\d+",
})
@Data
public class PanpanrPost extends SimpleModel implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        setReferUrl(page.getRequest().getUrl());
    }

}
