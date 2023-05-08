package com.klaxon.kserver.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class WebPageTaskCombineDto {

	private String thumbnail;
	private WebPageDto webPageDto;
	private WebPageTaskDto webPageTaskDto;
	private List<WebPageVideoTaskDto> webPageVideoTaskDtoList;

}
