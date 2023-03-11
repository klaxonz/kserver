package com.klaxon.kserver.controller;


import com.klaxon.kserver.advice.CustomSpringConfigurator;
import com.klaxon.kserver.advice.WebSocketMessageEncoder;
import com.klaxon.kserver.pojo.Response;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@NoArgsConstructor
@Component
@ServerEndpoint(value = "/ws/webpage/{id}", configurator = CustomSpringConfigurator.class, encoders = WebSocketMessageEncoder.class)
public class WebPageServerEndpoint {

    @Autowired
    private CacheManager cacheManager;


    private final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.debug("id: {}, session: {}", id, session);
        sessionMap.put(id, session);
        sendMessage();
    }

    @OnClose
    public void onClose() {

    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnError
    public void onError(Session session, Throwable error) {

    }

    public void sendMessage() {
        for (String id : sessionMap.keySet()) {
            Session session = sessionMap.get(id);
            if (!session.isOpen()) {
                continue;
            }
            Cache cache = cacheManager.getCache(id);
            Integer videoProgress = 0;
            Integer audioProgress = 0;
            Map<String, Integer> webpageProgress = new HashMap<>();
            if (!Objects.isNull(cache)) {
                Cache.ValueWrapper videoProgressCache = cache.get("videoProgress");
                Cache.ValueWrapper audioProgressCache = cache.get("audioProgress");
                if (!Objects.isNull(videoProgressCache)) {
                    videoProgress = (Integer) videoProgressCache.get();
                }
                if (!Objects.isNull(audioProgressCache)) {
                    audioProgress = (Integer) audioProgressCache.get();
                }
            }
            webpageProgress.put("videoProgress", videoProgress);
            webpageProgress.put("audioProgress", audioProgress);

            // 发送消息
            try {
                session.getBasicRemote().sendObject(Response.success(webpageProgress));
            } catch (Exception ex) {
                log.error("WebSocket推送失败: webPageId: {}", id, ex);
            }
        }
    }


}
