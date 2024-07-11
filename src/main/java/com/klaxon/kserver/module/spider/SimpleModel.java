package com.klaxon.kserver.module.spider;

import lombok.Data;
import us.codecraft.webmagic.model.annotation.ExtractBy;

import java.util.List;

@Data
public class SimpleModel {

    @ExtractBy(value = "https://www\\.(aliyundrive|alipan)\\.com/s/[a-zA-Z0-9]+", type = ExtractBy.Type.Regex)
    private List<String> shareUrl;

    private String referUrl;

}
