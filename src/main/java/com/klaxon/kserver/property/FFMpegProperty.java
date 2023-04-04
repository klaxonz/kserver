package com.klaxon.kserver.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Configuration
public class FFMpegProperty {

	@Value("${ffmpeg.path}")
	private String ffmpegPath;

}
