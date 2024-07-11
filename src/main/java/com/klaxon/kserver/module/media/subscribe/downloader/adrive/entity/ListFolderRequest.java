package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListFolderRequest {

    @JsonProperty("drive_id")
    private String driveId;
    @JsonProperty("parent_file_id")
    private String parentFileId;
    @JsonProperty("marker")
    private String marker;
    @JsonProperty("limit")
    private Integer limit;
    @JsonProperty("order_by")
    private String orderBy;
    @JsonProperty("order_direction")
    private String orderDirection;

}
