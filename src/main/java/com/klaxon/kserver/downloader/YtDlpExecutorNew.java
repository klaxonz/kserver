package com.klaxon.kserver.downloader;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class YtDlpExecutorNew {

    private final Config config;

    public YtDlpExecutorNew(Config config) {
        this.config = config;
    }

    private ProcessBuilder createProcessBuilder(String[] command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    public static BufferedReader getReader(Process process) {
        InputStream inputStream = process.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public boolean isSupported(String url) {
        // 构建命令
        String command = "yt-dlp -j $0";
        String[] commands = ShellCommandConverter.convert(command, url);

        String lines = Strings.EMPTY;
        try {
            // 执行命令
            ProcessBuilder processBuilder = createProcessBuilder(commands);
            Process process = processBuilder.start();

            // 获取执行结果
            BufferedReader bufferedReader = getReader(process);
            lines = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        // 解析命令结果
        return !lines.contains("Unsupported URL");
    }

    public List<VideoInfo> getVideoInfo(String url) throws IOException, InterruptedException {
        // 构建命令
        String command = "yt-dlp -j $0";
        String[] commands = ShellCommandConverter.convert(command, url);

        // 执行命令
        ProcessBuilder processBuilder = createProcessBuilder(commands);
        Process process = processBuilder.start();

        // 获取执行结果
        BufferedReader bufferedReader = getReader(process);
        List<String> infoList = bufferedReader.lines().collect(Collectors.toList());
        Iterator<String> iterator = infoList.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (!JSONUtil.isTypeJSON(next)) {
                iterator.remove();
            }
        }

        // 解析结果
        ObjectMapper objectMapper = new ObjectMapper();
        List<VideoInfo> videoInfoList = Lists.newArrayList();
        for (String videoInfoStr : infoList) {
            VideoInfo videoInfo = objectMapper.readValue(videoInfoStr, VideoInfo.class);
            videoInfoList.add(videoInfo);
        }

        process.waitFor();

        return videoInfoList;
    }

    public String downloadThumbnail(String url, String filename) throws IOException, InterruptedException {
        // 构建命令
        String savePath = Objects.isNull(config.getBasePath()) ? filename : config.getBasePath() + filename;
        String command = "yt-dlp --skip-download --write-thumbnail -o $0 $1";
        String[] commands = ShellCommandConverter.convert(command, savePath, url);

        // 执行命令
        ProcessBuilder processBuilder = createProcessBuilder(commands);
        Process process = processBuilder.start();

        // 获取执行结果
        BufferedReader bufferedReader = getReader(process);

        Optional<String> thumbnailOptional = bufferedReader.lines()
                .filter(line -> line.contains("Writing video thumbnail"))
                .map(line -> line.split("to:")[1].trim())
                .findFirst();

        String thumbnailPath = null;
        if (thumbnailOptional.isPresent()) {
            thumbnailPath = thumbnailOptional.get();
        }

        process.waitFor();

        return thumbnailPath.substring(config.getBasePath().length());
    }

    public String downloadFormat(String url, String format, String filename, DownloadCallback callback, MultiDownloadState state) throws IOException, InterruptedException {
        // 构建命令
        String savePath = Objects.isNull(config.getBasePath()) ? filename : config.getBasePath() + filename;
        String command = "yt-dlp -f $0 $1 --progress-template \"%(progress)j\" -o $2";
        String[] commands = ShellCommandConverter.convert(command, format, url, savePath);

        // 执行命令
        ProcessBuilder processBuilder = createProcessBuilder(commands);
        Process process = processBuilder.start();

        // 获取执行结果
        BufferedReader bufferedReader = getReader(process);

        String line;
        ObjectMapper objectMapper = new ObjectMapper();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("downloading")) {
                // 去除字符串开头的引号
                if (line.startsWith("\"")) {
                    line = line.substring(1);
                }
                if (line.endsWith("\"")) {
                    line = line.substring(0, line.length() - 1);
                }

                Progress progress = objectMapper.readValue(line, Progress.class);
                progress.setFormat(format);

                callback.onDownload(progress, state);
                filename = progress.getFilename();
            }
            if (line.contains("has already been download")) {
                String regex = "(?<=\\[download\\] ).+(?= has already been downloaded)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    filename = matcher.group();
                }
            }
        }

        process.waitFor();

        return filename.substring(config.getBasePath().length());
    }

}
