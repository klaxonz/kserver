package com.klaxon.kserver.constants;

import com.klaxon.kserver.property.AppProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class URLPathConstants {

    private final String webPageTaskImgPath = "web-page-task/img/";
    private final String webPageTaskVideoPath = "web-page-task/video/";

    @Resource
    private AppProperty property;

    public String getWebPageTaskImgUrl(Long taskId) {
        return property.getBaseUrl() + webPageTaskImgPath + taskId;
    }

    public String getWebPageTaskVideoPath(Long taskId) {
        return property.getBaseUrl() + webPageTaskVideoPath + taskId;
    }

}
