package com.klaxon.kserver.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TaskVo {


    /**
     * 主键id
     */
    private Object id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 网页id
     */
    private Integer webpageId;

    /**
     * 视频下载进度
     */
    private Integer videoProgress;

    /**
     * 音频下载进度
     */
    private Integer audioProgress;

    /**
     * 视频总长度
     */
    private Integer videoLength;

    /**
     * 音频总长度
     */
    private Integer audioLength;

    /**
     * 视频已下载长度
     */
    private Integer videoDownloadedLength;

    /**
     * 音频已下载长度
     */
    private Integer audioDownloadedLength;

    /**
     * 视频下载路径
     */
    private String videoPath;

    /**
     * 音频下载路径
     */
    private String audioPath;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;



}
