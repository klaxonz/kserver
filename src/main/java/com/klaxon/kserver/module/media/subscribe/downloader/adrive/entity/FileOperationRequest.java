package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FileOperationRequest {

    private String resource;
    private List<Request> requests;

    @Data
    public static class Request {
        private String id;
        private String method;
        private String url;
        private RequestBody body;
        private Map<String, Object> headers;
    }

    @Data
    public static class RequestBody {
        @JsonProperty("file_id")
        private String fileId;
        @JsonProperty("share_id")
        private String shareId;
        @JsonProperty("to_drive_id")
        private String toDriveId;
        @JsonProperty("to_parent_file_id")
        private String toParentFileId;
        @JsonProperty("auto_rename")
        private Boolean autoRename;
        @JsonProperty("async_task_id")
        private String asyncTaskId;
    }

}