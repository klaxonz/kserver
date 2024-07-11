package com.klaxon.kserver.module.media.subscribe.downloader.adrive;

import com.google.common.collect.Lists;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.FileOperationResponse;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.PathInfo;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.SaveFileResult;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ShareInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AdriveDownloader {

    @Resource
    private ApiManager apiManager;
    @Resource
    private DriveManager driveManager;

    public List<SaveFileResult> saveSharedLink(String url) {
        if (StringUtils.isBlank(url)) {
            return new ArrayList<>();
        }
        String shareId = getShareId(url);
        ShareInfo shareInfo = apiManager.shareInfo(shareId, null);
        if (Objects.isNull(shareInfo)) {
            return new ArrayList<>();
        }
        String shareToken = apiManager.shareToken(shareId, null);
        shareInfo.setShareId(shareId);
        shareInfo.setShareToken(shareToken);

        List<FileOperationResponse> fileOperationResponses = apiManager.copyFile(
                driveManager.getDefaultDriveId(), driveManager.getDefaultParentFileId(), shareInfo);

        List<SaveFileResult> results = Lists.newArrayList();
        for (FileOperationResponse fileOperationResponse : fileOperationResponses) {
            FileOperationResponse.Response response = fileOperationResponse.getResponses().get(0);

            SaveFileResult result = new SaveFileResult();
            result.setStatus(1);

            String fileId = response.getBody().getFileId();
            if (StringUtils.isBlank(fileId)) {
                result.setStatus(1);
                results.add(result);
                continue;
            }

            String asyncTaskId = response.getBody().getAsyncTaskId();
            if (StringUtils.isBlank(asyncTaskId)) {
                PathInfo pathInfo = apiManager.getPath(driveManager.getDefaultDriveId(), response.getBody().getFileId());
                PathInfo.FileInfo fileInfo = pathInfo.getItems().get(0);
                String fileName = fileInfo.getFileName();
                result.setFilePath(driveManager.getDefaultPath() + "/" + fileName);
                result.setStatus(0);

                results.add(result);
            } else {
                for (int i = 0; i < 10; i++) {
                    FileOperationResponse asyncTaskStatusResponse = apiManager.getAsyncTaskStatus(asyncTaskId);
                    FileOperationResponse.Response asyncTaskResponse = asyncTaskStatusResponse.getResponses().get(0);
                    String status = asyncTaskResponse.getBody().getStatus();
                    if (StringUtils.equals(status, "Succeed")) {
                        PathInfo pathInfo = apiManager.getPath(driveManager.getDefaultDriveId(), response.getBody().getFileId());
                        PathInfo.FileInfo fileInfo = pathInfo.getItems().get(0);
                        String fileName = fileInfo.getFileName();
                        result.setFilePath(driveManager.getDefaultPath() + "/" + fileName);
                        result.setStatus(0);
                        break;
                    }
                    try {
                        ThreadUtils.sleep(Duration.of(1, ChronoUnit.SECONDS));
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            results.add(result);
        }
        return results;
    }

    private static String getShareId(String url) {
        if (StringUtils.equals("/", String.valueOf(url.charAt(url.length() - 1)))) {
            url = url.substring(0, url.length() - 1);
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public ShareInfo getShareInfo(String shareUrl) {
        String shareId = getShareId(shareUrl);
        return apiManager.shareInfo(shareId, null);
    }

}