package com.klaxon.kserver.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.constants.URLPathConstants;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.converter.WebPageTaskMapperStruct;
import com.klaxon.kserver.converter.WebPageVideoTaskMapperStruct;
import com.klaxon.kserver.downloader.DownloadTask;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.service.dto.WebPageDto;
import com.klaxon.kserver.service.dto.WebPageTaskCombineDto;
import com.klaxon.kserver.service.dto.WebPageTaskDto;
import com.klaxon.kserver.service.dto.WebPageVideoTaskDto;
import com.klaxon.kserver.util.ThreadLocalHolder;

import cn.hutool.json.JSONNull;

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
	private WebPageVideoTaskMapper webPageVideoTaskMapper;
	@Resource
	private WebPageVideoTaskMapperStruct webPageVideoTaskMapperStruct;
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private ApplicationContext context;
	@Resource
	private URLPathConstants urlPathConstants;
	@Resource
	private YtDlpProperty ytDlpProperty;
	@Resource
	@Qualifier("videoDownloadTaskExecutor")
	private ThreadPoolTaskExecutor videoDownloadTaskExecutor;

	@Override
	@Transactional
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

			LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(WebPageVideoTask::getTaskId, item.getId())
					.orderByAsc(WebPageVideoTask::getVideoIndex);
			List<WebPageVideoTask> webPageVideoTasks = webPageVideoTaskMapper.selectList(queryWrapper);
			List<WebPageVideoTaskDto> webPageVideoTaskDtos = webPageVideoTaskMapperStruct.entitiesToDtos(webPageVideoTasks);
			webPageTaskCombineDto.setWebPageVideoTaskDtoList(webPageVideoTaskDtos);
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

		webPageTaskMapper.deleteById(task.getId());
		LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(WebPageVideoTask::getTaskId, task.getId());
		webPageVideoTaskMapper.delete(queryWrapper);

		String filePath = ytDlpProperty.getDestination() + task.getFilePath();
		if (StringUtils.isNotBlank(filePath)) {
			File file = new File(filePath);
			if (file.exists()) {
				try {
					FileUtils.forceDelete(file);
				} catch (IOException ignored) {
				}
			}
		}
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

	@Transactional
	@Override
	public WebPageVideoTask saveWebPageTask(WebPageTask task, Integer videoType, OnlineUser user, String thumbnailPath, Map<String, Object> videoInfo) {
		if (task.getId() == null) {
			task.setType(videoType);
			task.setStatus(1);
			webPageTaskMapper.insert(task);
		}

		Object playlistIndex = videoInfo.get("playlist_index");
		Integer index = playlistIndex instanceof JSONNull
				? 1
				: (Integer) videoInfo.getOrDefault("playlist_index", 1);

		LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(WebPageVideoTask::getTaskId, task.getId())
				.eq(WebPageVideoTask::getVideoIndex, index);
		WebPageVideoTask webPageVideoTask = webPageVideoTaskMapper.selectOne(queryWrapper);
		if (!Objects.isNull(webPageVideoTask)) {
			return webPageVideoTask;
		}

		webPageVideoTask = new WebPageVideoTask();
		webPageVideoTask.setTaskId(task.getId());
		webPageVideoTask.setUserId(user.getId());
		webPageVideoTask.setWebPageId(task.getWebPageId());
		webPageVideoTask.setThumbnailPath(thumbnailPath);
		webPageVideoTask.setVideoIndex(index);

		Object durationObj = videoInfo.get("duration");
		BigDecimal duration;
		if (durationObj instanceof Integer) {
			duration = new BigDecimal(String.valueOf(durationObj));
		} else {
			duration = (BigDecimal) videoInfo.get("duration");
		}
		webPageVideoTask.setVideoDuration(duration.intValue());

		String itemVideoTitle = (String) videoInfo.get("fulltitle");
		webPageVideoTask.setTitle(itemVideoTitle);

		List<Map<String, Object>> requestedFormats = (List<Map<String, Object>>) videoInfo.get("requested_formats");

		if (Objects.isNull(requestedFormats)) {
			Integer videoSize = videoInfo.containsKey("filesize") ? (Integer) videoInfo.get("filesize") : (Integer) videoInfo.get("filesize_approx");
			webPageVideoTask.setVideoLength(videoSize.longValue());
			webPageVideoTask.setVideoSize(videoSize.longValue());
			webPageVideoTask.setType(1);
		} else {
			Map<String, Object> videoFormat = requestedFormats.get(0);
			Map<String, Object> audioFormat = requestedFormats.get(1);
			Integer videoSize = videoFormat.containsKey("filesize") && !(videoFormat.get("filesize") instanceof JSONNull) && !(videoFormat.get("filesize") == null)
					? (Integer) videoFormat.get("filesize") : (Integer) videoFormat.get("filesize_approx");
			Integer audioSize = audioFormat.containsKey("filesize") && !(audioFormat.get("filesize") instanceof JSONNull) && !(audioFormat.get("filesize") == null)
					? (Integer) audioFormat.get("filesize") : (Integer) audioFormat.get("filesize_approx");
			webPageVideoTask.setVideoLength(videoSize.longValue());
			webPageVideoTask.setAudioLength(audioSize.longValue());
			long fileSize = videoSize + audioSize;
			webPageVideoTask.setVideoSize(fileSize);
			webPageVideoTask.setType(2);
		}

		webPageVideoTaskMapper.insert(webPageVideoTask);
		return webPageVideoTask;
	}
}
