package com.klaxon.kserver.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.klaxon.kserver.constants.URLPathConstants;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.converter.WebPageTaskMapperStruct;
import com.klaxon.kserver.downloader.DownloadTask;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.service.dto.WebPageDto;
import com.klaxon.kserver.service.dto.WebPageTaskCombineDto;
import com.klaxon.kserver.service.dto.WebPageTaskDto;
import com.klaxon.kserver.util.ThreadLocalHolder;

@Service("taskService")
public class WebPageTaskServiceImpl extends ServiceImpl<WebPageTaskMapper, WebPageTask>
		implements WebPageTaskService {

	@Resource
	private WebPageMapper webPageMapper;
	@Resource
	private WebPageMapperStruct webPageMapperStruct;
	@Resource
	private WebPageTaskMapper webPageTaskMapper;
	@Resource
	private WebPageTaskMapperStruct webPageTaskMapperStruct;
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private ApplicationContext context;
	@Resource
	private URLPathConstants urlPathConstants;
	@Resource
	@Qualifier("videoDownloadTaskExecutor")
	private ThreadPoolTaskExecutor videoDownloadTaskExecutor;

	@Override
	public List<WebPageTaskCombineDto> list(WebPageTaskDto webPageTaskDto) {

		Long userId = ThreadLocalHolder.getUser().getId();
		LambdaQueryWrapper<WebPageTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(WebPageTask::getUserId, userId).orderByDesc(WebPageTask::getCreateTime);
		List<WebPageTask> taskList = webPageTaskMapper.selectList(lambdaQueryWrapper);
		List<WebPageTaskDto> webPageTaskDtos = webPageTaskMapperStruct.entitiesToDtos(taskList);
		List<WebPageTaskCombineDto> combineDtos = Lists.newArrayList();
		for (WebPageTaskDto item : webPageTaskDtos) {
			WebPage webPage = webPageMapper.selectById(item.getWebPageId());
			WebPageDto webPageDto = webPageMapperStruct.entityToDto(webPage);
			WebPageTaskCombineDto webPageTaskCombineDto = new WebPageTaskCombineDto();
			webPageTaskCombineDto.setWebPageTaskDto(item);
			webPageTaskCombineDto.setWebPageDto(webPageDto);
			webPageTaskCombineDto.setThumbnail(urlPathConstants.getWebPageTaskImgUrl(item.getId()));
			combineDtos.add(webPageTaskCombineDto);
		}

		return combineDtos;
	}

	@Override
	public void retry(WebPageTaskDto webPageTaskDto) {
		WebPageTask task = webPageTaskMapper.selectById(webPageTaskDto.getId());
		WebPage webPage = webPageMapper.selectById(task.getWebPageId());
		DownloadTask downloadTask = context.getBean(DownloadTask.class, ThreadLocalHolder.getUser(), task, webPage);
		videoDownloadTaskExecutor.execute(downloadTask);
	}

	@Override
	public void pause(WebPageTaskDto webPageTaskDto) {
		WebPageTask task = webPageTaskMapper.selectById(webPageTaskDto.getId());
		String key = RedisKeyPrefixConstants.TASK_COMMAND_PREFIX + task.getId();
		redisTemplate.opsForValue().set(key, "pause");
	}

	@Override
	public void remove(WebPageTaskDto webPageTaskDto) {
		WebPageTask task = webPageTaskMapper.selectById(webPageTaskDto.getId());
		String key = RedisKeyPrefixConstants.TASK_COMMAND_PREFIX + task.getId();
		redisTemplate.opsForValue().set(key, "pause");

		String thumbnailPath = task.getThumbnailPath();
		if (StringUtils.isNotBlank(thumbnailPath)) {
			File file = new File(thumbnailPath);
			if (file.exists()) {
				File parentFile = file.getParentFile();
				try {
					FileUtils.forceDelete(parentFile);
				} catch (IOException ignored) {
				}
			}
		}
		webPageTaskMapper.deleteById(task.getId());
	}

	@Override
	public WebPageTaskCombineDto get(Long id) {
		WebPageTask task = webPageTaskMapper.selectById(id);
		WebPage webPage = webPageMapper.selectById(task.getWebPageId());
		WebPageDto webPageDto = webPageMapperStruct.entityToDto(webPage);
		WebPageTaskDto webPageTaskDto = webPageTaskMapperStruct.entityToDto(task);
		WebPageTaskCombineDto webPageTaskCombineDto = new WebPageTaskCombineDto();
		webPageTaskCombineDto.setWebPageDto(webPageDto);
		webPageTaskCombineDto.setWebPageTaskDto(webPageTaskDto);
		webPageTaskCombineDto.setThumbnail(urlPathConstants.getWebPageTaskVideoPath(id));
		return webPageTaskCombineDto;
	}
}
