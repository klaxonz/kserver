package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListFolderResponse {

    @JsonProperty("items")
    private List<FileInfo> items;
    @JsonProperty("next_marker")
    private String nextMarker;

    @Data
    public static class FileInfo {
        @JsonProperty("name")
        private String name;
        @JsonProperty("file_id")
        private String fileId;
    }
}
