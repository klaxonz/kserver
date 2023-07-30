package com.klaxon.kserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.service.dto.WebPageTaskCombineDto;
import com.klaxon.kserver.service.dto.WebPageTaskDto;

import java.util.List;

public interface WebPageTaskService extends IService<WebPageTask> {

	List<WebPageTaskCombineDto> list(WebPageTaskDto webPageTaskDto);

	void retry(WebPageTaskDto webPageTaskDto);

	void pause(WebPageTaskDto webPageTaskDto);

	void remove(WebPageTaskDto webPageTaskDto);

	WebPageTaskCombineDto get(Long id);

}
