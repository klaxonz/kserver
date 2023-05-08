package com.klaxon.kserver.timer;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.klaxon.kserver.controller.WebPageTaskListServerEndpoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebPageTaskTimer {

	@Resource
	private WebPageTaskListServerEndpoint webPageTaskListServerEndpoint;

	@Scheduled(fixedRate = 1000)
	public void sendTaskProgress() {
		webPageTaskListServerEndpoint.sendMessage();
	}

}
