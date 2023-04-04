package com.klaxon.kserver.downloader;

import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.controller.WebPageTaskListServerEndpoint;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;

public class YtDlpDownloadCallbackImpl implements YtDlpDownloadCallback {

	private final WebPageTask task;
	private final OnlineUser user;
	private final WebPageTaskMapper webPageTaskMapper;
	private final WebPageTaskListServerEndpoint webPageTaskListServerEndpoint;

	public YtDlpDownloadCallbackImpl(WebPageTaskMapper webPageTaskMapper, OnlineUser user, WebPageTask task,
			WebPageTaskListServerEndpoint webPageTaskListServerEndpoint) {
		this.task = task;
		this.user = user;
		this.webPageTaskMapper = webPageTaskMapper;
		this.webPageTaskListServerEndpoint = webPageTaskListServerEndpoint;
	}

	@Override
	public void onThumbnailSave(String path) {
		task.setThumbnailPath(path);
		webPageTaskMapper.updateById(task);
		webPageTaskListServerEndpoint.sendMessage();
	}

	@Override
	public void onProgress(YtDlpDownloadProgress progress) {
		String type = progress.getType();
		if (type.equals("video")) {
			task.setVideoProgress(progress.getPercent());
			task.setVideoDownloadEta(progress.getEta());
			task.setVideoDownloadedLength(progress.getDownloadedBytes());
			task.setVideoLength(progress.getTotalBytes());
			task.setVideoDownloadSpeed(progress.getDownloadSpeed());
			task.setVideoPath(progress.getFilepath());
		}
		if (type.equals("audio")) {
			task.setAudioProgress(progress.getPercent());
			task.setAudioDownloadEta(progress.getEta());
			task.setAudioDownloadedLength(progress.getDownloadedBytes());
			task.setAudioLength(progress.getTotalBytes());
			task.setAudioDownloadSpeed(progress.getDownloadSpeed());
			task.setAudioPath(progress.getFilepath());
		}
		task.setIsMerge(progress.getIsMerge());
		webPageTaskMapper.updateById(task);

	}

	@Override
	public void onFinish(String filepath) {
		task.setFilePath(filepath);
		Integer isMerge = task.getIsMerge();
		if (isMerge == 1) {
			task.setAudioProgress(10000);
		}
		task.setVideoProgress(10000);
		webPageTaskMapper.updateById(task);
		webPageTaskListServerEndpoint.sendMessage(user.getId());
	}

	@Override
	public void onGetDuration(int duration) {
		task.setVideoDuration(duration);
		webPageTaskMapper.updateById(task);
	}

	@Override
	public void beforeVideoDownload() {
		if (task.getId() == null) {
			webPageTaskMapper.insert(task);
		}
	}

	@Override
	public void onGetSize(long size) {
		task.setVideoSize(size);
		webPageTaskMapper.updateById(task);
	}

	@Override
	public void onGetResolution(int width, int height) {
		task.setWidth(width);
		task.setHeight(height);
		webPageTaskMapper.updateById(task);
	}
}
