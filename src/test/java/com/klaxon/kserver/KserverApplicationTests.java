package com.klaxon.kserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class KserverApplicationTests {

	public static void main(String[] args) {
		contextLoads();
	}

	static void contextLoads() {

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(
				"yt-dlp",
				"--encoding=UTF-8",
				"-N",
				"20",
				"--cookies-from-browser",
				"vivaldi:D:\\Program\\Scoop\\apps\\vivaldi\\current\\User Data",
				"-f",
				"bv",
//				"--progress-template",
//				"\"%(progress)j\"",
				"https://www.youtube.com/watch?v=Y4XuR-vFuco"
				);
		try {
			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			List<String> collect = bufferedReader.lines().collect(Collectors.toList());
			process.waitFor();

		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

}
