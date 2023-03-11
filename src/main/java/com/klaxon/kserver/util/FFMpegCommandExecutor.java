package com.klaxon.kserver.util;

import com.ffmpeg.common.common.StreamHanlerCommon;
import com.klaxon.kserver.property.FFMpegProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FFMpegCommandExecutor {

    @Autowired
    private FFMpegProperty ffmpegProperty;

    public void execute(String[] commands) throws IOException {
        List<String> executeCommand = Stream.of(commands).collect(Collectors.toList());
        executeCommand.add(0, ffmpegProperty.getFfmpegPath());
        ProcessBuilder builder = new ProcessBuilder(executeCommand);
        Process process = builder.start();
        StreamHanlerCommon.closeStreamQuietly(process);
    }

}
