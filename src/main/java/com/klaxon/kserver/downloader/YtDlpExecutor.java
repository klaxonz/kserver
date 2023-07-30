package com.klaxon.kserver.downloader;

import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.util.ProcessBuilderHelper;
import com.klaxon.kserver.util.ProcessCommandLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class YtDlpExecutor {

    private final Logger log = LoggerFactory.getLogger(YtDlpExecutor.class);


    private final YtDlpProperty ytDlpProperty;


    public YtDlpExecutor(YtDlpProperty ytDlpProperty) {
        this.ytDlpProperty = ytDlpProperty;
    }

    private List<String> buildBaseCommand() {
        List<String> baseCommand = new ArrayList<>();
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

        return baseCommand;
    }

    private ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command().addAll(buildBaseCommand());
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    public boolean isSupported(String url) {
        try {
            ProcessBuilder processBuilder = createProcessBuilder();
            List<String> command = processBuilder.command();
            command.add("-F");
            command.add(url);

            Process process = processBuilder.start();
            BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);

            String line;
            boolean showSeparator = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("---------------")) {
                    showSeparator = true;
                } else {
                    if (showSeparator) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ProcessBuilder forGetTitle(String url) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("--get-title");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    private ProcessBuilder forGetDuration(String url) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("--get-duration");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    private ProcessBuilder forGetInfo(String url) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("-j");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    private ProcessBuilder forGetFormat(String url) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("--get-format");
        command.add("bv+ba/best");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    private ProcessBuilder forGetThumbnail(String url, String basePath) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("--skip-download");
        command.add("--write-thumbnail");
        command.add("-o");
        command.add(basePath + "%(title)s_ytdl.%(ext)s");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    public ProcessBuilder forGetMedia(String url, String type, String basePath) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
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
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    private ProcessBuilder forGetVideoSize(String url) {
        ProcessBuilder builder = createProcessBuilder();
        List<String> command = builder.command();
        command.add("-f");
        command.add("bv+ba/best");
        command.add("--print");
        command.add("\"%(filesize,filesize_approx)s\"");
        command.add(url);
        ProcessCommandLogger.printCommand(command);
        return builder;
    }

    public String getVideoTitle(String url) throws InterruptedException, IOException {
        StringBuilder title = new StringBuilder();
        ProcessBuilder processBuilder = forGetTitle(url);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);
        bufferedReader.lines().forEach(title::append);
        process.waitFor();
        return title.toString();
    }

    public long getVideoSize(String url) throws InterruptedException, IOException {
        StringBuilder size = new StringBuilder();
        ProcessBuilder processBuilder = forGetVideoSize(url);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);
        bufferedReader.lines().forEach(size::append);
        process.waitFor();
        return Long.parseLong(size.toString());
    }

    public Integer getVideoDuration(String url) throws InterruptedException, IOException {
        StringBuilder duration = new StringBuilder();
        ProcessBuilder processBuilder = forGetDuration(url);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);
        bufferedReader.lines().forEach(duration::append);

        process.waitFor();
        return durationToSeconds(duration.toString());
    }

    private int durationToSeconds(String duration) {
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


    public String getVideoThumbnail(String url, String basePath) throws InterruptedException, IOException {
        String thumbnailPath = null;
        ProcessBuilder processBuilder = forGetThumbnail(url, basePath);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);

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
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);
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

}
