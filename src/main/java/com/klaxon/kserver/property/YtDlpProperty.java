package com.klaxon.kserver.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class YtDlpProperty {

	@Value("${yt-dlp.destination}")
	private String destination;

	@Value("${yt-dlp.cookies-path}")
	private String cookiesPath;

	@Value("${yt-dlp.cookies-from-browser}")
	private String cookiesFromBrowser;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getCookiesPath() {
		return cookiesPath;
	}

	public void setCookiesPath(String cookiesPath) {
		this.cookiesPath = cookiesPath;
	}

	public String getCookiesFromBrowser() {
		return cookiesFromBrowser;
	}

	public void setCookiesFromBrowser(String cookiesFromBrowser) {
		this.cookiesFromBrowser = cookiesFromBrowser;
	}
}
