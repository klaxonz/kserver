package com.klaxon.kserver.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.klaxon.kserver.pojo.Response;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

@Slf4j
public class WebSocketMessageEncoder implements Encoder.Text<Response> {
    @Override
    public String encode(Response message) throws EncodeException {
        try {
            JsonMapper jsonMapper = new JsonMapper();
            return jsonMapper.writeValueAsString(message);
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