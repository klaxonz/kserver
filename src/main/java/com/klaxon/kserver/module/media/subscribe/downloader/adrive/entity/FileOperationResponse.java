package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FileOperationResponse {

    private List<Response> responses;

    @Data
    public static class Response {
        private String id;
        private Integer status;
        private ResponseBody body;
    }

    @Data
    public static class ResponseBody {
        @JsonProperty("async_task_id")
        private String asyncTaskId;
        @JsonProperty("file_id")
        private String fileId;
        private String message;
        private String status;
    }

}
