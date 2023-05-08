package com.klaxon.kserver.mapper.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klaxon.kserver.bean.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网页视频存储任务表
 * 
 * @TableName t_task
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_web_page_task")
public class WebPageTask extends PageParam implements Serializable {

	private static final long serialVersionUID = -8634202805696729427L;

	@TableId(value = "id")
	private Long id;
	private Long userId;
	private Long webPageId;
	private Integer videoProgress;
	private Long videoDownloadedSize;
	private String filePath;
	private String thumbnailPath;

	private Long videoSize;
	private Integer videoDuration;
	private Integer type;
	private Integer status;

	private LocalDateTime createTime;
	private LocalDateTime updateTime;

}