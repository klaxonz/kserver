package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShareInfo {

    private String shareId;
    private String shareToken;
    @JsonProperty("file_infos")
    private List<FileInfo> fileInfos = new ArrayList<>();

    // 2: 成功，3：取消，4：禁止
    private Integer status;

    @Data
    public static class FileInfo {
        @JsonProperty("file_name")
        private String fileName;
        @JsonProperty("file_id")
        private String fileId;
        @JsonProperty("type")
        private String type;
    }
}