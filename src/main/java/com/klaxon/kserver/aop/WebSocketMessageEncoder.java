package com.klaxon.kserver.aop;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.util.BaseJacksonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketMessageEncoder implements Encoder.Text<Response> {
	@Override
	public String encode(Response message) throws EncodeException {
		try {
			ObjectMapper objectMapper = BaseJacksonUtil.getObjectMapper();
			return objectMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			log.error("JSON parse error", e);
		}
		return null;
	}

	@Override
	public void init(EndpointConfig endpointConfig) {

	}

	@Override
	public void destroy() {

	}
}