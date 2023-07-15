package com.klaxon.kserver.downloader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.property.YtDlpProperty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class YtDlpDownloader {

	private final Logger log = LoggerFactory.getLogger(YtDlpDownloader.class);

	private final YtDlpProperty ytDlpProperty;
	private final YtDlpDownloadCallback callback;
	private final WebPageTask task;
	private final ProcessBuilder builder;
	private final RedisTemplate<String, String> redisTemplate;
	private final boolean isRetry;
	List<String> baseCommand = new ArrayList<>();
	private String basePath;
	private Process process;
	private WebPageVideoTask webPageVideoTask;

	public YtDlpDownloader(YtDlpProperty property, WebPageTask task, RedisTemplate<String, String> redisTemplate,
			YtDlpDownloadCallback callback) {
		this.ytDlpProperty = property;
		this.callback = callback;
		this.task = task;
		this.redisTemplate = redisTemplate;

		baseCommand.add("yt-dlp");
		baseCommand.add("--encoding=UTF-8");
		if (StringUtils.isNotBlank(ytDlpProperty.getCookiesPath())) {
			File file = new File(ytDlpProperty.getCookiesPath());
			if (file.exists()) {
				baseCommand.add("--cookies");
				baseCommand.add(ytDlpProperty.getCookiesPath());
			}
		}
		if (StringUtils.isNotBlank(ytDlpProperty.getCookiesFromBrowser())) {
			String browserCookiePath = ytDlpProperty.getCookiesFromBrowser();
			int index = browserCookiePath.indexOf(":");
			if (index > 0) {
				browserCookiePath = browserCookiePath.substring(index + 1);
				File file = new File(browserCookiePath);
				if (file.exists()) {
					baseCommand.add("--cookies-from-browser");
					baseCommand.add(ytDlpProperty.getCookiesFromBrowser());
				}
			}
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
		printCommand(command);
		return builder;
	}

	private ProcessBuilder forGetDuration(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--get-duration");
		command.add(url);
		printCommand(command);
		return builder;
	}

	private ProcessBuilder forGetInfo(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("-j");
		command.add(url);
		printCommand(command);
		return builder;
	}

	private ProcessBuilder forGetFormat(String url) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("--get-format");
		command.add("bv+ba/best");
		command.add(url);
		printCommand(command);
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
		printCommand(command);
		return builder;
	}

	private ProcessBuilder forGetMedia(String url, String type) {
		List<String> command = builder.command();
		command.clear();
		command.addAll(baseCommand);
		command.add("-N");
		command.add(String.valueOf(4));
		command.add("-f");

		String savePath = basePath + "%(title)s.%(ext)s";
		if (StringUtils.equals(type, "video")) {
			command.add("bv");
			savePath = basePath + "%(title)s.%(ext)s.video";
		} else if (StringUtils.equals(type, "audio")){
			command.add("ba");
			savePath = basePath + "%(title)s.%(ext)s.audio";
		} else {
			command.add("best");
		}
		command.add("--progress-template");
		command.add("\"%(progress)j\"");
		command.add("-o");
		command.add(savePath);
		command.add(url);
		printCommand(command);
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
		printCommand(command);
		return builder;
	}

	private void printCommand(List<String> commands)  {
		String commandOutput = String.join(" ", commands);
		log.info("Execute command: {}", commandOutput);
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

	public long getVideoSize(String url) throws InterruptedException, IOException {
		StringBuilder size = new StringBuilder();
		ProcessBuilder processBuilder = forGetVideoSize(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		bufferedReader.lines().forEach(size::append);
		process.waitFor();
		return Long.parseLong(size.toString());
	}

	public Integer getVideoDuration(String url) throws InterruptedException, IOException {
		StringBuilder duration = new StringBuilder();
		ProcessBuilder processBuilder = forGetDuration(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		bufferedReader.lines().forEach(duration::append);

		process.waitFor();
		return durationToSeconds(duration.toString());
	}

	public String getVideoThumbnail(String url) throws InterruptedException, IOException {
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
			thumbnailPath = thumbnailPath.substring(ytDlpProperty.getDestination().length());
		}

		return thumbnailPath;
	}

	public List<String> getVideoInfo(String url) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = forGetInfo(url);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		List<String> infoList = bufferedReader.lines().collect(Collectors.toList());
		Iterator<String> iterator = infoList.iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (!JSONUtil.isTypeJSON(next)) {
				iterator.remove();
			}
		}
		process.waitFor();
		return infoList;
	}

	public void getBestMergeVideo(String url) throws InterruptedException, IOException, ExecutionException {

		if (webPageVideoTask.getType() == 1) {
			String videoPath = downloadMedia(url, "best");
			webPageVideoTask.setVideoPath(videoPath);
			webPageVideoTask.setFilePath(videoPath);
			callback.updateWebPageVideoTask(webPageVideoTask);
		} else {
			String videoPath = downloadMedia(url, "video");
			webPageVideoTask.setVideoPath(videoPath);
			callback.updateWebPageVideoTask(webPageVideoTask);

			String audioPath = downloadMedia(url, "audio");
			webPageVideoTask.setAudioPath(audioPath);
			callback.updateWebPageVideoTask(webPageVideoTask);

			String videoFilePath = ytDlpProperty.getDestination() + videoPath;
			if (StringUtils.isNotBlank(audioPath)) {
				String audioFilePath = ytDlpProperty.getDestination() + audioPath;
				String mergeFilePath = mergeVideoAndAudio(videoFilePath, audioFilePath);
				FileUtils.forceDelete(new File(videoFilePath));
				FileUtils.forceDelete(new File(audioFilePath));

				videoFilePath = videoFilePath.substring(0, videoFilePath.lastIndexOf(".video"));
				FileUtils.deleteQuietly(new File(videoFilePath));
				FileUtils.moveFile(new File(mergeFilePath), new File(videoFilePath), StandardCopyOption.REPLACE_EXISTING);

				videoPath = videoFilePath.substring(ytDlpProperty.getDestination().length());
			}
			webPageVideoTask.setFilePath(videoPath);
			callback.updateWebPageVideoTask(webPageVideoTask);
		}
	}

	public String mergeVideoAndAudio(String videoFilePath, String audioFilePath) throws InterruptedException, IOException {
		String mergeFilePath = videoFilePath + ".merge";
		File mergeFile = new File(mergeFilePath);
		if (mergeFile.exists()) {
			FileUtils.forceDelete(mergeFile);
		}
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(
				"ffmpeg",
				"-i", videoFilePath,
				"-i", audioFilePath,
				"-c:v", "copy",
				"-c:a", "copy",
				"-f", "mp4",
				mergeFilePath);
		printCommand(processBuilder.command());

		processBuilder.redirectErrorStream(true);
		process = processBuilder.start();
		BufferedReader bufferedReader = getReader(process);
		List<String> logs = bufferedReader.lines().collect(Collectors.toList());
		process.waitFor();
		return mergeFilePath;
	}

	class LogCallable implements Callable<String> {

		private String type;
		private InputStream inputStream;

		public LogCallable(InputStream inputStream, String type) {
			this.type = type;
			this.inputStream = inputStream;
		}

		@Override
		public String call() {
			String filepath = null;
			try {
				String line;
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				while ((line = bufferedReader.readLine()) != null) {
					checkExit();
					if (line.contains("downloading")) {
						// 去除字符串开头的引号
						if (line.startsWith("\"")) {
							line = line.substring(1);
						}
						if (line.endsWith("\"")) {
							line = line.substring(0, line.length() - 1);
						}
						HashMap<String, Object> hashMap = JSONUtil.toBean(line, HashMap.class);
						YtDlpDownloadProgress progress = extractProgress(hashMap, type);
						setProgressCache(webPageVideoTask, progress);
						filepath = progress.getFilepath();
					}
					if (line.contains("has already been download")) {
						String regex = "(?<=\\[download\\] ).+(?= has already been downloaded)";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							filepath = matcher.group().trim();
							filepath = filepath.substring(ytDlpProperty.getDestination().length());
							long totalSize = type.equals("audio")
									? webPageVideoTask.getAudioLength()
									: webPageVideoTask.getVideoLength();
							YtDlpDownloadProgress progress = new YtDlpDownloadProgress(type, 10000, totalSize,
									webPageVideoTask.getVideoSize(), 0, 0, filepath);
							setProgressCache(webPageVideoTask, progress);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return filepath;
		}
	}

	private String downloadMedia(String url, String type) throws IOException, InterruptedException, ExecutionException {
		ProcessBuilder processBuilder = forGetMedia(url, type);
		process = processBuilder.start();

		FutureTask<String> futureTask = new FutureTask<>(new LogCallable(process.getInputStream(), type));
		Thread logThread = new Thread(futureTask);
		logThread.start();
		String filepath = futureTask.get();

		if (process != null) {
			process.waitFor();
		}
		return filepath;
	}

	private YtDlpDownloadProgress extractProgress(Map<String, Object> map, String type) {
		long downloadedBytes = Long.parseLong(map.getOrDefault("downloaded_bytes", 0).toString());
		Long totalBytes = extractTotalBytes(map);
		Long eta = extractEta(map);
		Long speed = extractSpeed(map);
		int percent = extractPercent(downloadedBytes, totalBytes);
		String filename = (String) map.get("filename");
		filename = filename.substring(ytDlpProperty.getDestination().length());
		return new YtDlpDownloadProgress(type, percent, downloadedBytes, totalBytes, speed, eta,
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
				"-i", ytDlpProperty.getDestination() + webPageVideoTask.getFilePath());
		printCommand(processBuilder.command());

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
		callback.onGetResolution(webPageVideoTask, width, height);
	}

	public void download(String url) throws IOException, InterruptedException, ExecutionException {
		List<String> videoInfoList = getVideoInfo(url);

		String firstVideoInfoStr = videoInfoList.get(0);
		Map firstVideoInfo = JSONUtil.toBean(firstVideoInfoStr, Map.class);
		String videoTitle = firstVideoInfo.containsKey("playlist_title")
				? (String) firstVideoInfo.get("playlist_title")
				: (String) firstVideoInfo.get("title");
		setBasePath(videoTitle);

		Integer videoType = videoInfoList.size() > 1 ? 1 : 0;

		long totalVideoSize = 0L;
		BigDecimal totalDuration = new BigDecimal("0");
		for (String videoInfoStr : videoInfoList) {
			Map videoInfo = JSONUtil.toBean(videoInfoStr, Map.class);
			Integer videoSize = (Integer) videoInfo.get("filesize_approx");
			totalVideoSize += videoSize;
			Object durationObj = videoInfo.get("duration");
			BigDecimal duration;
			if (durationObj instanceof Integer) {
				duration = new BigDecimal(String.valueOf(durationObj));
			} else {
				duration = (BigDecimal) videoInfo.get("duration");
			}
			totalDuration = totalDuration.add(duration);
		}

		task.setFilePath(basePath.substring(ytDlpProperty.getDestination().length()));
		task.setVideoSize(totalVideoSize);
		task.setVideoDuration(totalDuration.intValue());
		callback.updateWebPageTask(task);

		for (String videoInfoStr : videoInfoList) {
			Map videoInfo = JSONUtil.toBean(videoInfoStr, Map.class);
			String videoUrl = (String) videoInfo.get("original_url");

			String thumbnailPath = getVideoThumbnail(videoUrl);
			webPageVideoTask = callback.beforeVideoDownload(videoType, videoInfo, thumbnailPath);

			getBestMergeVideo(videoUrl);
			getVideoResolution();
		}
		task.setStatus(2);
		task.setVideoSize(task.getVideoDownloadedSize());
		task.setVideoProgress(10000);
		callback.updateWebPageTask(task);
	}

	private void setBasePath(String title) {
		String destination = ytDlpProperty.getDestination();
		if (isRetry) {
			String thumbnailPath = task.getThumbnailPath();
			if (StringUtils.isNotBlank(thumbnailPath)) {
				File file = new File(thumbnailPath);
				String parent = file.getParent();
				basePath = parent + File.separator;
			} else {
				basePath = destination + escapeFileName(title) + File.separator;
			}
		} else {
			basePath = destination + escapeFileName(title) + File.separator;
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

	public void setProgressCache(WebPageVideoTask webPageVideoTask, YtDlpDownloadProgress progress) {
		String key = "task:progress:" + webPageVideoTask.getId();
		String progressJson = JSONUtil.toJsonStr(progress);
		redisTemplate.opsForValue().set(key, progressJson, 30, TimeUnit.MINUTES);
	}

}
