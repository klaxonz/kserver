package com.klaxon.kserver.service.dto;

import java.util.List;


public class WebPageTaskCombineDto {

	private String thumbnail;
	private WebPageDto webPageDto;
	private WebPageTaskDto webPageTaskDto;
	private List<WebPageVideoTaskDto> webPageVideoTaskDtoList;

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public WebPageDto getWebPageDto() {
		return webPageDto;
	}

	public void setWebPageDto(WebPageDto webPageDto) {
		this.webPageDto = webPageDto;
	}

	public WebPageTaskDto getWebPageTaskDto() {
		return webPageTaskDto;
	}

	public void setWebPageTaskDto(WebPageTaskDto webPageTaskDto) {
		this.webPageTaskDto = webPageTaskDto;
	}

	public List<WebPageVideoTaskDto> getWebPageVideoTaskDtoList() {
		return webPageVideoTaskDtoList;
	}

	public void setWebPageVideoTaskDtoList(List<WebPageVideoTaskDto> webPageVideoTaskDtoList) {
		this.webPageVideoTaskDtoList = webPageVideoTaskDtoList;
	}
}
