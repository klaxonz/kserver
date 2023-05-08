package com.klaxon.kserver.controller;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.klaxon.kserver.aop.CustomSpringConfigurator;
import com.klaxon.kserver.aop.WebSocketMessageEncoder;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.controller.vo.WebPageTaskCombineVo;
import com.klaxon.kserver.controller.vo.WebPageTaskVo;
import com.klaxon.kserver.controller.vo.WebPageVideoTaskVo;
import com.klaxon.kserver.controller.vo.WebPageVo;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.converter.WebPageTaskMapperStruct;
import com.klaxon.kserver.converter.WebPageVideoTaskMapperStruct;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.service.dto.WebPageDto;
import com.klaxon.kserver.service.dto.WebPageTaskCombineDto;
import com.klaxon.kserver.service.dto.WebPageTaskDto;
import com.klaxon.kserver.service.dto.WebPageVideoTaskDto;
import com.klaxon.kserver.util.ThreadLocalHolder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Component
@ServerEndpoint(value = "/ws/task/list", configurator = CustomSpringConfigurator.class, encoders = WebSocketMessageEncoder.class)
public class WebPageTaskListServerEndpoint {

	private final ConcurrentHashMap<Long, OnlineUser> userMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();

	@Resource
	private WebPageTaskService webPageTaskService;
	@Resource
	private WebPageMapperStruct webPageMapperStruct;
	@Resource
	private WebPageTaskMapperStruct webPageTaskMapperStruct;
	@Resource
	private WebPageVideoTaskMapperStruct webPageVideoTaskMapperStruct;

	@OnOpen
	public void onOpen(Session session) {
		Long userId = ThreadLocalHolder.getUser().getId();
		userMap.put(userId, ThreadLocalHolder.getUser());
		sessionMap.put(userId, session);
		sendMessage();
	}

	@OnClose
	public void onClose(Session session) {
		for (Long id : sessionMap.keySet()) {
			Session closeSession = sessionMap.get(id);
			if (session == closeSession) {
				sessionMap.remove(id);
				userMap.remove(id);
			}
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
	}

	@OnError
	public void onError(Session session, Throwable error) {
		Long userId = ThreadLocalHolder.getUser().getId();
		userMap.remove(userId);
		sessionMap.remove(userId);
	}

	public synchronized void sendMessage() {
		for (Long id : sessionMap.keySet()) {
			getAndSendMessage(id);
		}
	}

	public synchronized void sendMessage(Long userId) {
		for (Long id : sessionMap.keySet()) {
			if (Objects.equals(userId, id)) {
				getAndSendMessage(id);
			}
		}
	}

	private void getAndSendMessage(Long userId) {
		Session session = sessionMap.get(userId);
		if (!session.isOpen()) {
			return;
		}
		ThreadLocalHolder.setUser(userMap.get(userId));
		List<WebPageTaskCombineDto> list = webPageTaskService.list(new WebPageTaskDto());
		List<WebPageTaskCombineVo> webPageTaskCombineVos = Lists.newArrayList();
		for (WebPageTaskCombineDto webPageTaskCombineDto : list) {
			WebPageDto webPageDto = webPageTaskCombineDto.getWebPageDto();
			WebPageTaskDto webPageTaskDto = webPageTaskCombineDto.getWebPageTaskDto();
			List<WebPageVideoTaskDto> webPageVideoTaskDtoList = webPageTaskCombineDto.getWebPageVideoTaskDtoList();
			WebPageVo webPageVo = webPageMapperStruct.dtoToVo(webPageDto);
			WebPageTaskVo webPageTaskVo = webPageTaskMapperStruct.dtoToVo(webPageTaskDto);
			List<WebPageVideoTaskVo> webPageVideoTaskVos = webPageVideoTaskMapperStruct.dtosToVos(webPageVideoTaskDtoList);
			WebPageTaskCombineVo webPageTaskCombineVo = new WebPageTaskCombineVo();
			webPageTaskCombineVo.setWebPage(webPageVo);
			webPageTaskCombineVo.setWebPageTask(webPageTaskVo);
			webPageTaskCombineVo.setThumbnail(webPageTaskCombineDto.getThumbnail());
			webPageTaskCombineVos.add(webPageTaskCombineVo);
			webPageTaskCombineVo.setWebPageVideoTaskList(webPageVideoTaskVos);
		}
		try {
			session.getBasicRemote().sendObject(Response.success(webPageTaskCombineVos));
		} catch (Exception ex) {
			log.error("WebSocket推送失败: userId: {}", userId, ex);
		}
	}

}
