package com.klaxon.kserver.downloader;

import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DownloadTask implements Runnable {

	private final Logger log = LoggerFactory.getLogger(DownloadTask.class);

	private final Config config;
	private final WebPage webPage;
	private final OnlineUser user;
	private final DownloadCallback callback;


	public DownloadTask(OnlineUser user, Config config,  WebPage webPage, DownloadCallback callback) {
		this.user = user;
		this.config = config;
		this.webPage = webPage;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			ThreadLocalHolder.setUser(user);
			ThreadLocalHolder.setWebPage(webPage);
			Downloader downloader = new YtDlpDownloaderNew(config, callback);
			downloader.download(webPage.getUrl());
		} catch (Exception ex) {
			log.error("download task error", ex);
		} finally {
			ThreadLocalHolder.removeUser();
			ThreadLocalHolder.removeWebPage();
		}
	}
}
