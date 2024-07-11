package com.klaxon.kserver.module.media.subscribe.downloader.adrive;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class TokenManager {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    @Value("${alipan.refresh-token}")
    private String refreshToken;

    private String accessToken;

    @Resource
    private OkHttpClient client;
    @Resource
    private ObjectMapper objectMapper;

    public synchronized String getAccessToken() {
        if (accessToken == null || accessTokenExpired()) {
            accessToken = refreshToken();
        }
        return accessToken;
    }

    private boolean accessTokenExpired() {
        // Add logic to check if the current access token has expired.
        // This can be based on the token's expiry time if available.
        return false;
    }

    private String refreshToken() {
        Map<String, Object> data = new HashMap<>();
        data.put("refresh_token", refreshToken);

        String url = "https://api.aliyundrive.com/token/refresh";
        Request request = buildRequest(url, data);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Request to {} failed: {}", url, response);
                return null;
            }
            String responseJson = Objects.requireNonNull(response.body()).string();
            Map<String, Object> responseMap = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {});
            if (responseMap != null) {
                RefreshTokenInfo tokenInfo = objectMapper.convertValue(responseMap, RefreshTokenInfo.class);
                return tokenInfo.getAccessToken();
            }
        } catch (IOException e) {
            log.error("Failed to refresh access token", e);
        }
        return null;
    }

    private Request buildRequest(String url, Map<String, Object> data) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, serializeToJson(data));
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    private String serializeToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize to JSON", e);
        }
    }

    @Data
    public static class RefreshTokenInfo {
        @JsonProperty("access_token")
        private String accessToken;
    }
}