package com.klaxon.kserver.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FFmpegExecutor {


    public static VideoResolution getVideoResolution(String path) throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                "ffprobe",
                "-show_streams",
                "-v", "error",
                "-print_format", "json",
                "-i", path);

        ProcessCommandLogger.printCommand(processBuilder.command());

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);

        String line;
        StringBuilder videoJsonStr = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            videoJsonStr.append(line);
        }
        process.waitFor();

        JSONObject jsonObject = new JSONObject(videoJsonStr);
        Integer width = (Integer) JSONUtil.getByPath(jsonObject, "streams[0].width");
        Integer height = (Integer) JSONUtil.getByPath(jsonObject, "streams[0].height");

        return new VideoResolution(width, height);
    }


    public static String mergeVideoAndAudio(String videoFilePath, String audioFilePath) throws InterruptedException, IOException {
        String mergeFilePath = videoFilePath.substring(0, videoFilePath.lastIndexOf("."));
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

        ProcessCommandLogger.printCommand(processBuilder.command());

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = ProcessBuilderHelper.getReader(process);
        List<String> logs = bufferedReader.lines().collect(Collectors.toList());

        process.waitFor();

        FileUtils.deleteQuietly(new File(videoFilePath));
        FileUtils.deleteQuietly(new File(audioFilePath));

        return mergeFilePath;
    }


}
