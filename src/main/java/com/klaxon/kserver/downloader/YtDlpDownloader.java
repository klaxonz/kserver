package com.klaxon.kserver.downloader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.property.YtDlpProperty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YtDlpDownloader {

	private final YtDlpProperty ytDlpProperty;
	private final YtDlpDownloadCallback callback;
	private final WebPageTask task;
	private final ProcessBuilder builder;
	private final RedisTemplate<String, String> redisTemplate;
	private final boolean isRetry;
	List<String> baseCommand = new ArrayList<>();
	private String basePath;
	private boolean isMerge = false;
	private boolean hasDownloadVideo = false;
	private Process process;

	public YtDlpDownloader(YtDlpProperty property, WebPageTask task, RedisTemplate<String, String> redisTemplate,
			YtDlpDownloadCallback callback) {
		this.ytDlpProperty = property;
		this.callback = callback;
		this.task = task;
		this.redisTemplate = redisTemplate;

		int cores = Runtime.getRuntime().availableProcessors() / 2;
		baseCommand.add("yt-dlp");
		baseCommand.add("--encoding=UTF-8");
		baseCommand.add("-N");
		baseCommand.add(String.valueOf(cores));
		if (StringUtils.isNotBlank(ytDlpProperty.getCookiesPath())) {
			baseCommand.add("--cookies");
			baseCommand.add(ytDlpProperty.getCookiesPath());
		} else if (StringUtils.isNotBlank(ytDlpProperty.getCookiesFromBrowser())) {
			baseCommand.add("--cookies-from-browser");
			baseCommand.add(ytDlpProperty.getCookiesFromBrowser());
		}

		this.builder = createProcessBuilder();

		isRetry = task.getId() != null;
	}

	public static String escapeFileName(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return fileName;
		}
		String illegalChars = "[/\\\\?%*:|\"<>]"; // 定义特殊字符
		String escapedChars = "_"; // 定义特殊字符转义为 _
		return fileName.replaceAll(illegalChars, escapedChars);
	}

	public static int durationToSeconds(String duration) {
		String[] parts = duration.split(":");
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if (parts.length == 1) {
			seconds = Integer.parseInt(parts[0]);
		}
		if (parts.length == 2) {
			seconds = Integer.parseInt(parts[1]);
			minutes = Integer.parseInt(parts[0]);
		}
		if (parts.length == 3) {
			seconds = Integer.parseInt(parts[2]);
			minutes = Integer.parseInt(parts[1]);
			hours = Integer.parseInt(parts[0]);
		}
		return hours * 3600 + minutes * 60 + seconds;
	}

	private ProcessBuilder createProcessBuilder() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command().addAll(baseCommand);
		processBuilder.redirectErrorStream(true);
		return processBuilder;
	}

	private BufferedReader getReader(Process process) {
		InputStream inputStream = process.getInputStream();
		return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	}

	private ProcessBuilder forGetTitle(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--get-title");
		command.add(url);
		return builder;
	}

	private ProcessBuilder forGetDuration(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--get-duration");
		command.add(url);
		return builder;
	}

	private ProcessBuilder forGetFormat(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--get-format");
		command.add("bv+ba/best");
		command.add(url);
		return builder;
	}

	private ProcessBuilder forGetThumbnail(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--skip-download");
		command.add("--write-thumbnail");
		command.add("-o");
		command.add(basePath + "%(title)s_ytdl.%(ext)s");
		command.add(url);
		return builder;
	}

	private ProcessBuilder forGetVideo(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("-f");
		command.add("bv+ba/best");
		command.add("--embed-thumbnail");
		command.add("--progress-template");
		command.add("\"%(progress)j\"");
		command.add("-o");
		command.add(basePath + "%(title)s.%(ext)s");
		command.add(url);
		return builder;
	}

	private ProcessBuilder forGetVideoSize(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("-f");
		command.add("bv+ba/best");
		command.add("--print");
		command.add("\"%(filesize,filesize_approx)s\"");
		command.add(url);
		return builder;
	}

	public String getVideoTitle(String url) throws InterruptedException, IOException {
		StringBuilder title = new StringBuilder();
		ProcessBuilder processBuilder = forGetTitle(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		bufferedReader.lines().forEach(title::append);
		process.waitFor();
		return title.toString();
	}

	public void getVideoSize(String url) throws InterruptedException, IOException {
		StringBuilder size = new StringBuilder();
		ProcessBuilder processBuilder = forGetVideoSize(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		bufferedReader.lines().forEach(size::append);
		process.waitFor();
		callback.onGetSize(Long.parseLong(size.toString()));
	}

	public void getVideoDuration(String url) throws InterruptedException, IOException {
		StringBuilder duration = new StringBuilder();
		ProcessBuilder processBuilder = forGetDuration(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		bufferedReader.lines().forEach(duration::append);

		process.waitFor();
		int seconds = durationToSeconds(duration.toString());
		callback.onGetDuration(seconds);
	}

	public void getVideoThumbnail(String url) throws InterruptedException, IOException {
		String thumbnailPath = null;
		ProcessBuilder processBuilder = forGetThumbnail(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);

		Optional<String> thumbnailOptional = bufferedReader.lines()
				.filter(line -> line.contains("Writing video thumbnail"))
				.map(line -> line.split("to:")[1].trim())
				.findFirst();
		if (thumbnailOptional.isPresent()) {
			thumbnailPath = thumbnailOptional.get();
		}
		if (process != null) {
			process.waitFor();
		}
		if (StringUtils.isNotBlank(thumbnailPath)) {
			callback.onThumbnailSave(thumbnailPath);
		}
	}

	public void getBestMergeVideo(String url) throws InterruptedException, IOException {
		String filepath = null;
		ProcessBuilder processBuilder = forGetVideo(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			checkExit();
			if (line.contains("format(s):") && line.contains("+")) {
				isMerge = true;
			}
			if (line.contains("downloading")) {
				HashMap<String, Object> hashMap = JSONUtil.toBean(line, HashMap.class);
				YtDlpDownloadProgress progress = extractProgress(hashMap);
				callback.onProgress(progress);
				filepath = progress.getFilepath();
			}
			if (line.contains("Merging formats")) {
				filepath = line.split("\"")[1];
			}
		}
		if (process != null) {
			process.waitFor();
		}
		if (StringUtils.isNotBlank(filepath)) {
			callback.onFinish(filepath);
		}
	}

	private YtDlpDownloadProgress extractProgress(Map<String, Object> map) {
		long downloadedBytes = Long.parseLong(map.getOrDefault("downloaded_bytes", 0).toString());
		Long totalBytes = extractTotalBytes(map);
		Long eta = extractEta(map);
		Long speed = extractSpeed(map);
		int percent = extractPercent(downloadedBytes, totalBytes);
		String filename = (String) map.get("filename");
		String type;
		if (!hasDownloadVideo) {
			type = "video";
			hasDownloadVideo = percent == 10000;
		} else {
			type = "audio";
		}
		return new YtDlpDownloadProgress(type, isMerge ? 1 : 0, percent, downloadedBytes, totalBytes, speed, eta,
				filename);
	}

	private Long extractTotalBytes(Map<String, Object> map) {
		return map.containsKey("total_bytes")
				? Long.parseLong(map.get("total_bytes").toString())
				: ((BigDecimal) map.get("total_bytes_estimate")).longValue();
	}

	private Long extractEta(Map<String, Object> map) {
		Object eta = map.get("eta");
		if (eta == null) {
			return 0L;
		}
		if (!eta.equals("null")) {
			return 0L;
		}
		return Long.parseLong(eta.toString());
	}

	private Long extractSpeed(Map<String, Object> map) {
		Object speed = map.get("speed");
		if (speed == null) {
			return 0L;
		}
		if (!speed.equals("null")) {
			return 0L;
		}
		return new BigDecimal(speed.toString()).longValue();
	}

	private Integer extractPercent(Long downloadedBytes, Long totalBytes) {
		return new BigDecimal(String.valueOf(downloadedBytes))
				.divide(new BigDecimal(String.valueOf(totalBytes)), 6, RoundingMode.CEILING)
				.multiply(new BigDecimal("10000")).intValue();
	}

	public void getVideoResolution() throws InterruptedException, IOException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(
				"ffprobe",
				"-show_streams",
				"-v", "error",
				"-print_format", "json",
				"-i", task.getFilePath());

		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);

		String line;
		StringBuilder videoJsonStr = new StringBuilder();

		while ((line = bufferedReader.readLine()) != null) {
			videoJsonStr.append(line);
		}
		process.waitFor();
		JSONObject jsonObject = new JSONObject(videoJsonStr);
		Integer width = (Integer) JSONUtil.getByPath(jsonObject, "streams[0].width");
		Integer height = (Integer) JSONUtil.getByPath(jsonObject, "streams[0].height");
		callback.onGetResolution(width, height);
	}

	public void download(String url) throws IOException, InterruptedException {
		String title = getVideoTitle(url);
		setBasePath(title);
		getVideoDuration(url);
		getVideoSize(url);
		getVideoThumbnail(url);
		callback.beforeVideoDownload();
		getBestMergeVideo(url);
		getVideoThumbnail(url);
		getVideoResolution();
	}

	private void setBasePath(String title) {
		String destination = ytDlpProperty.getDestination();
		if (isRetry) {
			String thumbnailPath = task.getThumbnailPath();
			if (StringUtils.isNotBlank(thumbnailPath)) {
				File file = new File(thumbnailPath);
				String parent = file.getParent();
				basePath = parent + File.separator;
			}
		} else {
			basePath = destination + escapeFileName(title) + File.separator;
			Path path = Paths.get(basePath);
			boolean exists = Files.exists(path);
			if (exists) {
				LocalDateTime now = LocalDateTime.now();
				String postfix = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				basePath = destination + escapeFileName(title) + "_" + postfix + File.separator;
			}
		}
	}

	public void checkExit() {
		String key = "task:command:" + task.getId();
		String command = redisTemplate.opsForValue().getAndExpire(key, 1, TimeUnit.SECONDS);
		if (StringUtils.equals(command, "pause")) {
			if (process != null) {
				process.destroy();
				throw new RuntimeException("pause download task");
			}
		}
	}

}
