package com.klaxon.kserver.downloader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.controller.WebPageTaskListServerEndpoint;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.service.WebPageTaskService;

public class YtDlpDownloadCallbackImpl implements YtDlpDownloadCallback {

	private final WebPageTask task;
	private final OnlineUser user;
	private final WebPageTaskService webPageTaskService;
	private final WebPageTaskMapper webPageTaskMapper;
	private final WebPageVideoTaskMapper webPageVideoTaskMapper;
	private final WebPageTaskListServerEndpoint webPageTaskListServerEndpoint;

	public YtDlpDownloadCallbackImpl(WebPageTaskMapper webPageTaskMapper, OnlineUser user, WebPageTask task, WebPageTaskService webPageTaskService,
									 WebPageVideoTaskMapper webPageVideoTaskMapper, WebPageTaskListServerEndpoint webPageTaskListServerEndpoint) {
		this.task = task;
		this.user = user;
		this.webPageTaskService = webPageTaskService;
		this.webPageTaskMapper = webPageTaskMapper;
		this.webPageVideoTaskMapper = webPageVideoTaskMapper;
		this.webPageTaskListServerEndpoint = webPageTaskListServerEndpoint;
	}

	@Override
	public void onThumbnailSave(WebPageVideoTask webPageVideoTask, String path) {
		webPageVideoTask.setThumbnailPath(path);
		webPageVideoTaskMapper.updateById(webPageVideoTask);
		webPageTaskListServerEndpoint.sendMessage();
	}

	@Override
	public void onProgress(WebPageVideoTask webPageVideoTask, YtDlpDownloadProgress progress) {
		String type = progress.getType();
		if (type.equals("video") || type.equals("best")) {
			webPageVideoTask.setVideoProgress(progress.getPercent());
			webPageVideoTask.setVideoDownloadEta(progress.getEta());
			webPageVideoTask.setVideoLength(progress.getTotalBytes());
			webPageVideoTask.setVideoDownloadedLength(progress.getDownloadedBytes());
			webPageVideoTask.setVideoDownloadSpeed(progress.getDownloadSpeed());
			webPageVideoTask.setVideoPath(progress.getFilepath());
		}
		if (type.equals("audio")) {
			webPageVideoTask.setAudioProgress(progress.getPercent());
			webPageVideoTask.setAudioDownloadEta(progress.getEta());
			webPageVideoTask.setAudioLength(progress.getTotalBytes());
			webPageVideoTask.setAudioDownloadedLength(progress.getDownloadedBytes());
			webPageVideoTask.setAudioDownloadSpeed(progress.getDownloadSpeed());
			webPageVideoTask.setAudioPath(progress.getFilepath());
		}
		webPageVideoTaskMapper.updateById(webPageVideoTask);

		Long videoDownloadedSize = webPageVideoTaskMapper.queryVideoDownloadedSize(task.getId());
		task.setVideoDownloadedSize(videoDownloadedSize);
		Long videoSize = task.getVideoSize();
		Integer videoDownloadProgress = new BigDecimal(String.valueOf(videoDownloadedSize))
				.divide(new BigDecimal(String.valueOf(videoSize)), 6, RoundingMode.CEILING)
				.multiply(new BigDecimal("10000")).intValue();
		task.setVideoProgress(videoDownloadProgress);
		webPageTaskMapper.updateById(task);
	}

	@Override
	public void onFinish(WebPageVideoTask webPageVideoTask, String filepath) {
		webPageVideoTask.setFilePath(filepath);
		Integer isMerge = webPageVideoTask.getIsMerge();
		if (isMerge == 1) {
			webPageVideoTask.setAudioProgress(10000);
		}
		webPageVideoTask.setVideoProgress(10000);
		webPageVideoTaskMapper.updateById(webPageVideoTask);
		webPageTaskListServerEndpoint.sendMessage(user.getId());
	}

	@Override
	public void onGetDuration(WebPageVideoTask webPageVideoTask, int duration) {
		webPageVideoTask.setVideoDuration(duration);
		webPageVideoTaskMapper.updateById(webPageVideoTask);
	}

	@Override
	public WebPageVideoTask beforeVideoDownload(Integer videoType, Map<String, Object> videoInfo, String thumbnailPath) {
		return webPageTaskService.saveWebPageTask(task, videoType, user, thumbnailPath, videoInfo);
	}

	@Override
	public void onGetSize(WebPageVideoTask webPageVideoTask, long size) {
		webPageVideoTask.setVideoSize(size);
		webPageVideoTaskMapper.updateById(webPageVideoTask);
	}

	@Override
	public void onGetResolution(WebPageVideoTask webPageVideoTask, int width, int height) {
		webPageVideoTask.setWidth(width);
		webPageVideoTask.setHeight(height);
		webPageVideoTaskMapper.updateById(webPageVideoTask);
	}

	@Override
	public void onCreatePlaylistTask(Map<String, Object> videoInfo) {

	}

	@Override
	public void updateWebPageTask(WebPageTask task) {
		webPageTaskMapper.updateById(task);
	}

	@Override
	public void updateWebPageVideoTask(WebPageVideoTask videoTask) {
		if (!Objects.isNull(videoTask) && !Objects.isNull(videoTask.getId())) {
			webPageVideoTaskMapper.updateById(videoTask);
		}
	}
}
