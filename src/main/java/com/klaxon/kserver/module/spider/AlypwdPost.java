package com.klaxon.kserver.module.spider;

import lombok.Data;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.List;

@TargetUrl("https://www.alypw.com/post/*")
@HelpUrl(value = {
        "https://www.alypw.com/category-2*",
        "https://www.alypw.com/category-15*",
        "https://www.alypw.com/tags-2*",
        "https://www.alypw.com/category-38*",
})
@Data
public class AlypwdPost implements AfterExtractor {

    @ExtractBy(value = "https://www\\.(aliyundrive|alipan)\\.com/s/[a-zA-Z0-9]+", type = ExtractBy.Type.Regex)
    private List<String> shareUrl;

    private String referUrl;

    @Override
    public void afterProcess(Page page) {
        this.referUrl = page.getRequest().getUrl();
    }
}
