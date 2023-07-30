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
	private Integer progress;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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

	public Integer getAudioProgress() {
		return audioProgress;
	}

	public void setAudioProgress(Integer audioProgress) {
		this.audioProgress = audioProgress;
	}

	public Long getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Long videoLength) {
		this.videoLength = videoLength;
	}

	public Long getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(Long audioLength) {
		this.audioLength = audioLength;
	}

	public Long getVideoDownloadedLength() {
		return videoDownloadedLength;
	}

	public void setVideoDownloadedLength(Long videoDownloadedLength) {
		this.videoDownloadedLength = videoDownloadedLength;
	}

	public Long getAudioDownloadedLength() {
		return audioDownloadedLength;
	}

	public void setAudioDownloadedLength(Long audioDownloadedLength) {
		this.audioDownloadedLength = audioDownloadedLength;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public Integer getProgress() {
		return progress;
	}
	public void setProgress(Integer progress) {
		this.progress = progress;
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

	public Integer getIsMerge() {
		return isMerge;
	}

	public void setIsMerge(Integer isMerge) {
		this.isMerge = isMerge;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getVideoIndex() {
		return videoIndex;
	}

	public void setVideoIndex(Integer videoIndex) {
		this.videoIndex = videoIndex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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