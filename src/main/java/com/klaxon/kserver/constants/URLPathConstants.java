package com.klaxon.kserver.constants;

import com.klaxon.kserver.property.AppProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class URLPathConstants {

    @Resource
    private AppProperty property;

    public String getWebPageTaskImgUrl(Long taskId) {
        String webPageTaskImgPath = "web-page-task/img/";
        return property.getBaseUrl() + webPageTaskImgPath + taskId;
    }

    public String getWebPageTaskVideoPath(Long taskId) {
        String webPageTaskVideoPath = "web-page-task/video/";
        return property.getBaseUrl() + webPageTaskVideoPath + taskId;
    }

}
