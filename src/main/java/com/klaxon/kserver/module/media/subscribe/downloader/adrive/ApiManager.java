package com.klaxon.kserver.module.media.subscribe.downloader.adrive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.FileOperationRequest;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.FileOperationResponse;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ListFolderRequest;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ListFolderResponse;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.PathInfo;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.ShareInfo;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ApiManager {

    private static final String BASE_URL = "https://api.aliyundrive.com";
    private static final String GET_PATH = "/adrive/v1/file/get_path";
    private static final String BATCH_OP = "/adrive/v4/batch";
    private static final String GET_USER_INFO = "/v2/user/get";
    private static final String LIST_FOLDER = "/adrive/v2/file/listFolder";
    private static final String GET_SHARE_TOKEN = "/v2/share_link/get_share_token";
    private static final String GET_SHARE_BY_ANONYMOUS = "/adrive/v3/share_link/get_share_by_anonymous";

    private static final Integer RETRY_TIMES = 10;

    @Resource
    private AdriveHttpClient client;
    @Resource
    private ObjectMapper objectMapper;

    public ShareInfo shareInfo(String shareId, String passwd) {
        if (StringUtils.isBlank(shareId)) {
            throw new IllegalArgumentException("shareId 不能为空");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("share_id", shareId);
        if (StringUtils.isNotBlank(passwd)) {
            data.put("share_pwd", "");
        }

        String url = String.format("%s%s?share_id=%s", BASE_URL, GET_SHARE_BY_ANONYMOUS, shareId);
        Map<String, Object> response;
        int requestCount = 0;
        do {
            response = client.post(url, data);
            requestCount++;
        } while (response == null && requestCount <  RETRY_TIMES);

        if (Objects.isNull(response)) {
            return null;
        }
        if (!response.containsKey("share_name")) {
            log.info("share link: {}, message: {}", shareId, response.get("message"));
            int status = 3;
            if (StringUtils.contains((String) response.get("message"), "forbidden")) {
                status = 4;
            }
            ShareInfo shareInfo = new ShareInfo();
            shareInfo.setStatus(status);
            return shareInfo;
        }

        ShareInfo shareInfo = objectMapper.convertValue(response, ShareInfo.class);

        if (shareInfo.getFileInfos().isEmpty()) {
            // 取消分享
            shareInfo.setStatus(5);
        } else {
            shareInfo.setStatus(2);
        }

        return shareInfo;
    }

    public String shareToken(String shareId, String passwd) {
        if (StringUtils.isBlank(shareId)) {
            throw new IllegalArgumentException("shareId 不能为空");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("share_id", shareId);
        if (StringUtils.isNotBlank(passwd)) {
            data.put("share_pwd", "");
        }

        String url = String.format("%s%s", BASE_URL, GET_SHARE_TOKEN);
        Map<String, Object> response = client.post(url, data);
        return (String) response.get("share_token");
    }

    public PathInfo getPath(String driveId, String fileId) {
        if (StringUtils.isBlank(driveId) || StringUtils.isBlank(fileId)) {
            throw new IllegalArgumentException("driveId 和 fileId 不能为空");
        }
        Map<String, Object> data = new HashMap<>();

        data.put("drive_id", driveId);
        data.put("file_id", fileId);

        String url = String.format("%s%s", BASE_URL, GET_PATH);
        Map<String, Object> response = client.post(url, data);

        return objectMapper.convertValue(response, PathInfo.class);
    }

    public UserInfo getUserInfo() {
        String url = String.format("%s%s", BASE_URL, GET_USER_INFO);
        Map<String, Object> response = client.post(url, Maps.newHashMap());
        return objectMapper.convertValue(response, UserInfo.class);
    }

    public ListFolderResponse listFolder(String driveId, String parentFileId, String nextMarker) {
        ListFolderRequest request = new ListFolderRequest();
        request.setDriveId(driveId);
        request.setParentFileId(parentFileId);
        request.setLimit(20);
        request.setOrderBy("updated_at");
        request.setOrderDirection("DESC");

        if (StringUtils.isNotBlank(nextMarker)) {
            request.setMarker(nextMarker);
        }

        String url = String.format("%s%s", BASE_URL, LIST_FOLDER);
        Map<String, Object> response = client.post(url, request);

        return objectMapper.convertValue(response, ListFolderResponse.class);
    }

    public List<FileOperationResponse> copyFile(String driveId, String parentFileId, ShareInfo shareInfo) {
        // 验证shareToken的有效性
        if (StringUtils.isBlank(shareInfo.getShareToken())) {
            throw new IllegalArgumentException("shareToken is invalid");
        }

        List<ShareInfo.FileInfo> fileInfos = shareInfo.getFileInfos();
        if (fileInfos == null || fileInfos.isEmpty()) {
            return Lists.newArrayList(); // 返回空列表，避免不必要的操作
        }

        List<FileOperationResponse> operationResponses = Lists.newArrayList();
        for (ShareInfo.FileInfo fileInfo : fileInfos) {
            List<FileOperationRequest.Request> requests = Lists.newArrayList();
            FileOperationRequest copyFileRequest = new FileOperationRequest();
            copyFileRequest.setResource("file");
            copyFileRequest.setRequests(requests);

            FileOperationRequest.Request request = new FileOperationRequest.Request();
            FileOperationRequest.RequestBody body = new FileOperationRequest.RequestBody();
            body.setFileId(fileInfo.getFileId());
            body.setToDriveId(driveId);
            body.setToParentFileId(parentFileId);
            body.setAutoRename(true);
            body.setShareId(shareInfo.getShareId());

            Map<String, Object> headers = new HashMap<>();
            headers.put("content-type", "application/json");

            request.setBody(body);
            request.setHeaders(headers);
            request.setId("0");
            request.setMethod("POST");
            request.setUrl("/file/copy");
            requests.add(request);

            Map<String, Object> httpHeaders = new HashMap<>();
            headers.put("x-share-token", shareInfo.getShareToken());

            String url = String.format("%s%s", BASE_URL, BATCH_OP);
            Map<String, Object> response = client.post(url, copyFileRequest, httpHeaders);
            FileOperationResponse operationResponse = objectMapper.convertValue(response, FileOperationResponse.class);

            operationResponses.add(operationResponse);
        }

        return operationResponses;
    }

    public FileOperationResponse getAsyncTaskStatus(String asyncTaskId) {
        // 验证asyncTaskId的合法性
        if (asyncTaskId == null || asyncTaskId.trim().isEmpty()) {
            throw new IllegalArgumentException("asyncTaskId cannot be null or empty");
        }

        List<FileOperationRequest.Request> requests = Lists.newArrayList();
        FileOperationRequest asyncTaskRequest = new FileOperationRequest();
        asyncTaskRequest.setResource("file");
        asyncTaskRequest.setRequests(requests);

        FileOperationRequest.Request request = new FileOperationRequest.Request();
        FileOperationRequest.RequestBody body = new FileOperationRequest.RequestBody();
        body.setAsyncTaskId(asyncTaskId);


        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "application/json");

        request.setBody(body);
        request.setHeaders(headers);
        request.setId("0");
        request.setMethod("POST");
        request.setUrl("/async_task/get");
        requests.add(request);

        String url = String.format("%s%s", BASE_URL, BATCH_OP);
        Map<String, Object> response = client.post(url, asyncTaskRequest);

        return objectMapper.convertValue(response, FileOperationResponse.class);
    }

}
