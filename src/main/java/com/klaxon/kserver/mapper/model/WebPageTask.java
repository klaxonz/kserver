package com.klaxon.kserver.mapper.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klaxon.kserver.bean.PageParam;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 网页视频存储任务表
 * 
 * @TableName t_task
 */

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

	private Long videoSize;
	private Integer videoDuration;
	private Integer type;
	private Integer status;

	private LocalDateTime createTime;
	private LocalDateTime updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getWebPageId() {
		return webPageId;
	}

	public void setWebPageId(Long webPageId) {
		this.webPageId = webPageId;
	}

	public Integer getVideoProgress() {
		return videoProgress;
	}

	public void setVideoProgress(Integer videoProgress) {
		this.videoProgress = videoProgress;
	}

	public Long getVideoDownloadedSize() {
		return videoDownloadedSize;
	}

	public void setVideoDownloadedSize(Long videoDownloadedSize) {
		this.videoDownloadedSize = videoDownloadedSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public Long getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(Long videoSize) {
		this.videoSize = videoSize;
	}

	public Integer getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(Integer videoDuration) {
		this.videoDuration = videoDuration;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
}