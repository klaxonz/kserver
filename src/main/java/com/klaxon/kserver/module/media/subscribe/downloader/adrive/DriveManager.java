package com.klaxon.kserver.module.media.subscribe.downloader.adrive;

import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ListFolderResponse;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DriveManager {

    private String defaultDriveId;
    private String defaultParentFileId;
    @Value("${adrive.default-path:/Temp}")
    private String defaultPath;

    @Resource
    private ApiManager apiManager;

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getDefaultDriveId() {
        if (StringUtils.isBlank(defaultDriveId)) {
            UserInfo userInfo = apiManager.getUserInfo();
            defaultDriveId = userInfo.getDriveId();
        }
        return defaultDriveId;
    }

    public synchronized String getDefaultParentFileId() {
        if (StringUtils.isNotBlank(defaultParentFileId)) {
            return defaultParentFileId;
        }

        String[] splits = defaultPath.split("/");
        List<String> paths = Arrays.stream(splits)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        String currentPath = "";
        String parentFileId = "root";
        String nextMarker = null;
        for (String path : paths) {
            currentPath += "/" + path;
            do {
                ListFolderResponse listFolderResponse = apiManager.listFolder(getDefaultDriveId(), parentFileId, nextMarker);
                nextMarker = listFolderResponse.getNextMarker();
                List<ListFolderResponse.FileInfo> files = listFolderResponse.getItems();
                for (ListFolderResponse.FileInfo file : files) {
                    String fileName = file.getName();
                    if (path.equals(fileName)) {
                        parentFileId = file.getFileId();
                        break;
                    }
                }
                if (StringUtils.equals(currentPath, defaultPath)) {
                    defaultParentFileId = parentFileId;
                    break;
                }
            } while (StringUtils.isNotBlank(nextMarker));

        }
        if (StringUtils.isBlank(defaultParentFileId)) {
            throw new RuntimeException("default path not found");
        }

        return defaultParentFileId;
    }

}
