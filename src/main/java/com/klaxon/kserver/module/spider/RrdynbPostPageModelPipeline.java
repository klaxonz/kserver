package com.klaxon.kserver.module.spider;

import com.klaxon.kserver.module.spider.service.DriveShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.Resource;


@Slf4j
@Component
public class RrdynbPostPageModelPipeline implements PageModelPipeline<RrdynbPost> {

    @Resource
    private DriveShareService driveShareService;

    @Override
    public void process(RrdynbPost rrdynbPost, Task task) {
        driveShareService.saveDriveShare(rrdynbPost.getReferUrl(), rrdynbPost.getShareUrl());
    }

}
