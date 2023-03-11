package com.klaxon.kserver.keyboard;

import com.jfposton.ytdlp.*;

public class KeyBoardTest {

    public static void main(String[] args){
        // Video url to download
        String videoUrl = "https://www.bilibili.com/video/BV17s4y187vG?spm_id_from=333.1007.tianma.3-1-5.click";

// Destination directory
        String directory = "D:\\Temp\\yt-dlp";

// Build request
        YtDlpRequest request = new YtDlpRequest(videoUrl, directory);
        request.setOption("retries", 10);		// --retries 10
        request.setOption("cookies-from-browser", "vivaldi:\"D:\\Program\\Scoop\\apps\\vivaldi\\current\\User Data\"");

// Make request and return response
        YtDlpResponse response = null;
        try {
            response = YtDlp.execute(request, new DownloadProgressCallback() {
                @Override
                public void onProgressUpdate(float progress, long etaInSeconds) {
                    System.out.println(String.valueOf(progress) + "%");
                }
            });
        } catch (YtDlpException e) {
            e.printStackTrace();
        }

// Response
        String stdOut = response.getOut(); // Executable output

    }

}
