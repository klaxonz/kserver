package com.klaxon.kserver.module.media.subscribe.downloader.adrive.entity;

import lombok.Data;

@Data
public class SaveFileResult {

    // 0 成功， 1 失败
    private Integer status;
    private String filePath;

}
