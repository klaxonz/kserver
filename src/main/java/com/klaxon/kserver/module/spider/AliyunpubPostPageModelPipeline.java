package com.klaxon.kserver.module.spider;

import com.klaxon.kserver.module.spider.service.DriveShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.Resource;


@Slf4j
@Component
public class AliyunpubPostPageModelPipeline implements PageModelPipeline<AliyunpubPost> {

    @Resource
    private DriveShareService driveShareService;

    @Override
    public void process(AliyunpubPost aliyunpubPost, Task task) {
        driveShareService.saveDriveShare(aliyunpubPost.getReferUrl(), aliyunpubPost.getShareUrl());
    }

}
