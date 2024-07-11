package com.klaxon.kserver.module.spider;

import com.klaxon.kserver.module.spider.service.DriveShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.Resource;


@Slf4j
@Component
public class AliyunpanPostPageModelPipeline implements PageModelPipeline<AliyunpanPost> {

    @Resource
    private DriveShareService driveShareService;

    @Override
    public void process(AliyunpanPost aliyunpanPost, Task task) {
        driveShareService.saveDriveShare(aliyunpanPost.getReferUrl(), aliyunpanPost.getShareUrl());
    }

}
