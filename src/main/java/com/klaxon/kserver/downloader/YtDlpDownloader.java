package com.klaxon.kserver.downloader;

import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.constants.WebPageTaskConstants;
import com.klaxon.kserver.constants.WebPageVideoTaskConstants;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.util.FFmpegExecutor;
import com.klaxon.kserver.util.VideoResolution;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class YtDlpDownloader {

	private final Logger log = LoggerFactory.getLogger(YtDlpDownloader.class);

	private final YtDlpProperty ytDlpProperty;
	private final YtDlpDownloadCallback callback;
	private final WebPageTask task;
	private final RedisTemplate<String, String> redisTemplate;

	private String basePath;

	public YtDlpDownloader(YtDlpProperty property, WebPageTask task, RedisTemplate<String, String> redisTemplate,
			YtDlpDownloadCallback callback) {
		this.ytDlpProperty = property;
		this.callback = callback;
		this.task = task;
		this.redisTemplate = redisTemplate;

	}


	public void download(String url) throws IOException, InterruptedException, ExecutionException {
		YtDlpExecutor ytDlpExecutor = new YtDlpExecutor(ytDlpProperty);

		// 获取视频信息
		List<String> videoInfoList = ytDlpExecutor.getVideoInfo(url);
		String videoTitle = getVideoTitle(videoInfoList);
		setBasePath(videoTitle);

		// 计算视频总大小、视频总时长
		long totalVideoSize = 0L;
		BigDecimal totalDuration = new BigDecimal("0");
		for (String videoInfoStr : videoInfoList) {
			Map videoInfo = JSONUtil.toBean(videoInfoStr, Map.class);

			totalVideoSize += (Integer) videoInfo.get("filesize_approx");

			Object durationObj = videoInfo.get("duration");
			BigDecimal duration = durationObj instanceof Integer
					? new BigDecimal(String.valueOf(durationObj))
					: (BigDecimal) videoInfo.get("duration");

			totalDuration = totalDuration.add(duration);
		}

		// 更新任务信息
		task.setFilePath(basePath.substring(ytDlpProperty.getDestination().length()));
		task.setVideoSize(totalVideoSize);
		task.setVideoDuration(totalDuration.intValue());
		callback.updateWebPageTask(task);

		// 下载视频
		Integer videoType = videoInfoList.size() > 1
				? WebPageTaskConstants.VIDEO_TYPE_PLAYLIST
				: WebPageTaskConstants.VIDEO_TYPE_SINGLE;

		for (String videoInfoStr : videoInfoList) {
			Map videoInfo = JSONUtil.toBean(videoInfoStr, Map.class);
			String videoUrl = (String) videoInfo.get("original_url");
			String thumbnailPath = ytDlpExecutor.getVideoThumbnail(videoUrl, basePath);
			WebPageVideoTask webPageVideoTask = callback.beforeVideoDownload(videoType, videoInfo, thumbnailPath);

			getBestMergeVideo(videoUrl, webPageVideoTask);

			String targetPath = ytDlpProperty.getDestination() + webPageVideoTask.getFilePath();
			VideoResolution videoResolution = FFmpegExecutor.getVideoResolution(targetPath);
			callback.onGetResolution(webPageVideoTask, videoResolution);
		}

		// 更新任务信息
		task.setStatus(2);
		task.setVideoSize(task.getVideoDownloadedSize());
		task.setVideoProgress(10000);
		callback.updateWebPageTask(task);
	}


	public void getBestMergeVideo(String url, WebPageVideoTask webPageVideoTask) throws InterruptedException, IOException, ExecutionException {

		if (Objects.equals(webPageVideoTask.getType(), WebPageVideoTaskConstants.VIDEO_TYPE_MERGE)) {
			String videoPath = downloadMedia(url, "best", webPageVideoTask);
			webPageVideoTask.setVideoPath(videoPath);
			webPageVideoTask.setFilePath(videoPath);
		}
		if (Objects.equals(webPageVideoTask.getType(), WebPageVideoTaskConstants.VIDEO_TYPE_SPLIT)){
			String videoPath = downloadMedia(url, "video", webPageVideoTask);
			String audioPath = downloadMedia(url, "audio", webPageVideoTask);
			webPageVideoTask.setVideoPath(videoPath);
			webPageVideoTask.setAudioPath(audioPath);

			String videoFilePath = ytDlpProperty.getDestination() + videoPath;
			if (StringUtils.isNotBlank(audioPath)) {
				String audioFilePath = ytDlpProperty.getDestination() + audioPath;
				String mergeFilePath = FFmpegExecutor.mergeVideoAndAudio(videoFilePath, audioFilePath);

				FileUtils.forceDelete(new File(videoFilePath));
				FileUtils.forceDelete(new File(audioFilePath));

				videoFilePath = videoFilePath.substring(0, videoFilePath.lastIndexOf(".video"));
				FileUtils.deleteQuietly(new File(videoFilePath));
				FileUtils.moveFile(new File(mergeFilePath), new File(videoFilePath), StandardCopyOption.REPLACE_EXISTING);

				videoPath = videoFilePath.substring(ytDlpProperty.getDestination().length());
			}
			webPageVideoTask.setFilePath(videoPath);
		}
		callback.updateWebPageVideoTask(webPageVideoTask);
	}

	private String downloadMedia(String url, String type, WebPageVideoTask webPageVideoTask) throws IOException, InterruptedException, ExecutionException {
		YtDlpExecutor ytDlpExecutor = new YtDlpExecutor(ytDlpProperty);
		ProcessBuilder processBuilder = ytDlpExecutor.forGetMedia(url, type, basePath);
		Process process = processBuilder.start();

		FutureTask<String> futureTask = new FutureTask<>(new LogCallable(process, type, task, webPageVideoTask, ytDlpProperty, redisTemplate));
		Thread logThread = new Thread(futureTask);
		logThread.start();
		String filepath = futureTask.get();

		process.waitFor();

		return filepath;
	}

	private static String getVideoTitle(List<String> videoInfoList) {
		String firstVideoInfoStr = videoInfoList.get(0);
		Map firstVideoInfo = JSONUtil.toBean(firstVideoInfoStr, Map.class);
		return firstVideoInfo.containsKey("playlist_title")
				? (String) firstVideoInfo.get("playlist_title")
				: (String) firstVideoInfo.get("title");
	}

	private void setBasePath(String title) {
		String destination = ytDlpProperty.getDestination();
		basePath = destination + escapeFileName(title) + File.separator;
	}

	public static String escapeFileName(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return fileName;
		}
		String illegalChars = "[/\\\\?%*:|\"<>]"; // 定义特殊字符
		String escapedChars = "_"; // 定义特殊字符转义为 _
		return fileName.replaceAll(illegalChars, escapedChars);
	}

}
