package com.klaxon.kserver.module.spider.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.spider.mapper.DriveShareMapper;
import com.klaxon.kserver.module.spider.model.entity.DriveShare;
import com.klaxon.kserver.module.spider.service.DriveShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DriveShareScrapeTask {

    @Resource
    private DriveShareMapper driveShareMapper;
    @Resource
    private DriveShareService driveShareService;

    public void scrape() {

        int batch = 50;
        Long nextCursor = 0L;
        do {

            List<DriveShare> driveShares = driveShareMapper.selectList(new LambdaQueryWrapper<DriveShare>()
                    .gt(DriveShare::getId, nextCursor)
                    .eq(DriveShare::getScrapeStatus, 1)
                    .last("LIMIT " + batch)
            );
            if (driveShares.isEmpty() || driveShares.size() < batch) {
                nextCursor = null;
            } else {
                nextCursor = driveShares.get(batch - 1).getId();
            }
            for (DriveShare driveShare : driveShares) {
                driveShareService.scrape(driveShare.getId(), driveShare.getShareUrl());
            }

        } while (Objects.nonNull(nextCursor));
    }

}
