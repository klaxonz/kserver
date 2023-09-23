package com.klaxon.kserver.downloader;

import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.constants.WebPageVideoTaskConstants;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.property.YtDlpProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogCallable implements Callable<String> {

    private String type;
    private Process process;
    private WebPageTask task;
    private WebPageVideoTask webPageVideoTask;
    private YtDlpProperty ytDlpProperty;
    private RedisTemplate<String, String> redisTemplate;

    public LogCallable(Process process, String type, WebPageTask task, WebPageVideoTask webPageVideoTask, YtDlpProperty  ytDlpProperty, RedisTemplate<String, String> redisTemplate) {
        this.type = type;
        this.process = process;
        this.task = task;
        this.webPageVideoTask = webPageVideoTask;
        this.ytDlpProperty = ytDlpProperty;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String call() {
        String filepath = null;
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            while ((line = bufferedReader.readLine()) != null) {
                if (checkExit()) {break;}
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

    private Long extractEta(Map<String, Object> map) {
        Object eta = map.get("eta");
        if (eta instanceof String) {
            try {
                return Long.parseLong((String) eta);
            } catch (NumberFormatException ignored) {
                // Do nothing, fall through to return 0L
            }
        }
        return 0L;
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

    private Long extractTotalBytes(Map<String, Object> map) {
        return map.containsKey("total_bytes")
                ? Long.parseLong(map.get("total_bytes").toString())
                : ((BigDecimal) map.get("total_bytes_estimate")).longValue();
    }

    public boolean checkExit() {
        String key = RedisKeyPrefixConstants.TASK_COMMAND_PREFIX + task.getId();
        String command = redisTemplate.opsForValue().getAndExpire(key, 1, TimeUnit.SECONDS);
        if (StringUtils.equals(command, WebPageVideoTaskConstants.TASK_COMMAND_PAUSE)) {
            if (process != null) {
                process.destroy();
                return true;
            }
        }
        return false;
    }

    public void setProgressCache(WebPageVideoTask webPageVideoTask, YtDlpDownloadProgress progress) {
        String key = RedisKeyPrefixConstants.TASK_PROGRESS_PREFIX + webPageVideoTask.getId();
        String progressJson = JSONUtil.toJsonStr(progress);
        redisTemplate.opsForValue().set(key, progressJson, 30, TimeUnit.MINUTES);
    }

}
