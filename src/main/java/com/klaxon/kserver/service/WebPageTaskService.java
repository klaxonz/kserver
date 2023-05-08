package com.klaxon.kserver.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.service.dto.WebPageTaskCombineDto;
import com.klaxon.kserver.service.dto.WebPageTaskDto;

public interface WebPageTaskService extends IService<WebPageTask> {

	List<WebPageTaskCombineDto> list(WebPageTaskDto webPageTaskDto);

	void retry(WebPageTaskDto webPageTaskDto);

	void pause(WebPageTaskDto webPageTaskDto);

	void remove(WebPageTaskDto webPageTaskDto);

	WebPageTaskCombineDto get(Long id);

	WebPageVideoTask saveWebPageTask(WebPageTask task, Integer videoType, OnlineUser user, String thumbnailPath, Map<String, Object> videoInfo);
}
