package com.klaxon.kserver.aop;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.util.BaseJacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketMessageEncoder implements Encoder.Text<Response> {

	private final Logger log = LoggerFactory.getLogger(WebSocketMessageEncoder.class);

	@Override
	public String encode(Response message) {
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