package com.klaxon.kserver.downloader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.controller.WebPageTaskListServerEndpoint;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.util.ThreadLocalHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DownloadTask implements Runnable {

	private final WebPageTask task;
	private final WebPage webPage;
	private final OnlineUser user;

	@Resource
	private WebPageTaskMapper webPageTaskMapper;
	@Resource
	private WebPageVideoTaskMapper webPageVideoTaskMapper;
	@Resource
	private YtDlpProperty ytDlpProperty;
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private WebPageTaskListServerEndpoint webPageTaskListServerEndpoint;
	@Resource
	private WebPageTaskService webPageTaskService;

	public DownloadTask() {
		this.task = null;
		this.user = null;
		this.webPage = null;
		this.webPageTaskMapper = null;
		this.ytDlpProperty = null;
		this.redisTemplate = null;
		this.webPageTaskListServerEndpoint = null;
	}

	public DownloadTask(OnlineUser user, WebPageTask task, WebPage webPage) {
		this.user = user;
		this.task = task;
		this.webPage = webPage;
	}

	@PostConstruct
	private void init() {
		if (task != null && webPage != null && user != null) {
			task.setWebPageId(webPage.getId());
			task.setUserId(user.getId());
		}
	}

	@Override
	public void run() {
		try {
			ThreadLocalHolder.setUser(user);
			YtDlpDownloader downloader = new YtDlpDownloader(ytDlpProperty, task, redisTemplate,
					new YtDlpDownloadCallbackImpl(webPageTaskMapper, user, task, webPageTaskService, webPageVideoTaskMapper, webPageTaskListServerEndpoint));
			assert webPage != null;
			downloader.download(webPage.getUrl());
		} catch (Exception ex) {
			log.error("download task error", ex);
		}
	}
}
