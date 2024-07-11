package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PathInfo {

    @JsonProperty("items")
    private List<FileInfo> items;

    @Data
    public static class FileInfo {
        @JsonProperty("name")
        private String fileName;
        @JsonProperty("file_id")
        private String fileId;
    }

}
