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
@TableName(value = "t_web_page_video_task")
public class WebPageVideoTask extends PageParam implements Serializable {

	private static final long serialVersionUID = -8634202805696729427L;

	@TableId(value = "id")
	private Long id;
	private Long taskId;
	private Long userId;
	private Long webPageId;
	private Integer videoProgress;
	private Integer audioProgress;
	private Long videoLength;
	private Long audioLength;
	private Long videoDownloadedLength;
	private Long audioDownloadedLength;
	private String videoPath;
	private String audioPath;
	private String filePath;
	private String thumbnailPath;

	private Long videoSize;
	private Integer videoDuration;
	private Integer isMerge;
	private Integer width;
	private Integer height;
	private Integer videoIndex;
	private String title;
	private Integer type;

	private LocalDateTime createTime;
	private LocalDateTime updateTime;

}