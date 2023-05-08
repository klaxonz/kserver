package com.klaxon.kserver.controller.vo;

import java.util.List;

import lombok.Data;

@Data
public class WebPageTaskCombineVo {

	private String thumbnail;
	private WebPageVo webPage;
	private WebPageTaskVo webPageTask;
	private List<WebPageVideoTaskVo> webPageVideoTaskList;

}
