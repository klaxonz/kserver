package com.klaxon.kserver.module.spider.service;

import java.util.List;

public interface DriveShareService {

    void saveDriveShare(String referUrl, List<String> shareUrls);

    void scrape(Long shareId, String shareUrl);

}
