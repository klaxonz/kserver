package com.klaxon.kserver.module.spider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.AdriveDownloader;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ShareInfo;
import com.klaxon.kserver.module.spider.mapper.DriveShareAdriveDetailMapper;
import com.klaxon.kserver.module.spider.mapper.DriveShareMapper;
import com.klaxon.kserver.module.spider.mapper.DriveShareReferWebsiteMapper;
import com.klaxon.kserver.module.spider.model.entity.DriveShare;
import com.klaxon.kserver.module.spider.model.entity.DriveShareAdriveDetail;
import com.klaxon.kserver.module.spider.model.entity.DriveShareReferWebsite;
import com.klaxon.kserver.module.spider.service.DriveShareService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DriveShareServiceImpl implements DriveShareService {

    @Resource
    private AdriveDownloader adriveDownloader;
    @Resource
    private DriveShareMapper driveShareMapper;
    @Resource
    private DriveShareReferWebsiteMapper driveShareReferWebsiteMapper;
    @Resource
    private DriveShareAdriveDetailMapper driveShareAdriveDetailMapper;


    @Override
    public void saveDriveShare(String referUrl, List<String> shareUrls) {
        if (Objects.nonNull(shareUrls) && !shareUrls.isEmpty()) {
            shareUrls = shareUrls.stream().distinct().collect(Collectors.toList());

            for (String shareUrl : shareUrls) {
                DriveShare driveShare = driveShareMapper.selectOne(new LambdaQueryWrapper<DriveShare>()
                        .eq(DriveShare::getShareUrl, shareUrl)
                        .eq(DriveShare::getDriveType, 1));
                if (Objects.isNull(driveShare)) {
                    driveShare = new DriveShare();
                    driveShare.setDriveType(1);
                    driveShare.setShareUrl(shareUrl);
                    driveShareMapper.insert(driveShare);
                }

                DriveShareReferWebsite driveShareReferWebsite = driveShareReferWebsiteMapper.selectOne(new LambdaQueryWrapper<DriveShareReferWebsite>()
                        .eq(DriveShareReferWebsite::getReferUrl, referUrl)
                        .eq(DriveShareReferWebsite::getShareUrl, shareUrl)
                        .eq(DriveShareReferWebsite::getDriveType, 1)
                );
                if (Objects.isNull(driveShareReferWebsite)) {
                    driveShareReferWebsite = new DriveShareReferWebsite();
                    driveShareReferWebsite.setShareId(driveShare.getId());
                    driveShareReferWebsite.setDriveType(1);
                    driveShareReferWebsite.setShareUrl(shareUrl);
                    driveShareReferWebsite.setReferUrl(referUrl);
                    driveShareReferWebsiteMapper.insert(driveShareReferWebsite);
                }
            }
        }
    }

    @Override
    @Async
    public void scrape(Long shareId, String shareUrl) {
        ShareInfo shareInfo = adriveDownloader.getShareInfo(shareUrl);
        if (Objects.isNull(shareInfo)) {
            return;
        }
        if (shareInfo.getStatus().equals(3) || shareInfo.getStatus().equals(4) || shareInfo.getStatus().equals(5)) {
            DriveShare driveShare = new DriveShare();
            driveShare.setId(shareId);
            driveShare.setScrapeStatus(shareInfo.getStatus());
            driveShareMapper.updateById(driveShare);
            return;
        }

        List<ShareInfo.FileInfo> fileInfos = shareInfo.getFileInfos();
        for (ShareInfo.FileInfo fileInfo : fileInfos) {
            String fileName = fileInfo.getFileName();
            String type = fileInfo.getType();
            Integer typeNumber = StringUtils.equals(type, "folder") ? 2 : 1;

            DriveShareAdriveDetail driveShareAdriveDetail = driveShareAdriveDetailMapper.selectOne(new LambdaQueryWrapper<DriveShareAdriveDetail>()
                    .eq(DriveShareAdriveDetail::getShareId, shareId)
                    .eq(DriveShareAdriveDetail::getFileType, typeNumber)
                    .eq(DriveShareAdriveDetail::getName, fileName)
            );
            if (Objects.isNull(driveShareAdriveDetail)) {
                driveShareAdriveDetail = new DriveShareAdriveDetail();
                driveShareAdriveDetail.setShareId(shareId);
                driveShareAdriveDetail.setShareUrl(shareUrl);
                driveShareAdriveDetail.setName(fileName);
                driveShareAdriveDetail.setFileType(typeNumber);
                try {
                    driveShareAdriveDetailMapper.insert(driveShareAdriveDetail);
                } catch (DuplicateKeyException ignored) {
                }
            }
        }
        if (!fileInfos.isEmpty()) {
            DriveShare driveShare = new DriveShare();
            driveShare.setId(shareId);
            driveShare.setScrapeStatus(2);
            driveShareMapper.updateById(driveShare);
        }
    }


}
