package com.klaxon.kserver.controller.vo;

import java.util.List;


public class WebPageTaskCombineVo {

	private String thumbnail;
	private WebPageVo webPage;
	private WebPageTaskVo webPageTask;
	private List<WebPageVideoTaskVo> webPageVideoTaskList;

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public WebPageVo getWebPage() {
		return webPage;
	}

	public void setWebPage(WebPageVo webPage) {
		this.webPage = webPage;
	}

	public WebPageTaskVo getWebPageTask() {
		return webPageTask;
	}

	public void setWebPageTask(WebPageTaskVo webPageTask) {
		this.webPageTask = webPageTask;
	}

	public List<WebPageVideoTaskVo> getWebPageVideoTaskList() {
		return webPageVideoTaskList;
	}

	public void setWebPageVideoTaskList(List<WebPageVideoTaskVo> webPageVideoTaskList) {
		this.webPageVideoTaskList = webPageVideoTaskList;
	}
}
