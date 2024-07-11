package com.klaxon.kserver.module.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.TokenManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class AgentHttpClient {

    private static final String BASE_URL = "http://localhost:8080";

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OkHttpClient client;

    private Request.Builder createRequestBuilder(String url) {
        Request.Builder builder = new Request.Builder().url(url)
                .addHeader("content-type", "application/json");
        return builder;
    }

    public String get(String url) {
        Request.Builder builder = createRequestBuilder(url);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
        } catch (IOException e) {
            log.info("Http request failed, {}", url, e);
        }
        return null;
    }

    public String post(String url, Object body) {
        Request.Builder builder = createRequestBuilder(url);
        Request request = builder.build();

        if (body != null) {
            try {
                String bodyJson = objectMapper.writeValueAsString(body);
                RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), bodyJson);
                builder.post(requestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                if (body != null) {
                    return responseBodyString;
                }
            }
        } catch (IOException e) {
            log.info("Http request failed, {}", url, e);
        }
        return null;
    }


}
