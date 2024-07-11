package com.klaxon.kserver.module.media.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import cn.zhxu.data.Mapper;
import cn.zhxu.okhttps.HTTP;
import cn.zhxu.okhttps.HttpResult;
import cn.zhxu.okhttps.jackson.JacksonMsgConvertor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.service.AlistService;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AlistServiceImpl implements AlistService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Data
    public static class ListResponse {

        private ListResponse.ResponseData data;

        @Data
        public static class ResponseData {
            private List<DataContent> content;
        }

        @Data
        public static class DataContent {
            @JsonProperty("is_dir")
            private Boolean isDir;
            private String name;
        }
    }


    @SneakyThrows
    @Override
    public void getFileList(MediaLibrary mediaLibrary, String parentPath, List<String> filePaths) {
        String token = getToken(mediaLibrary.getId(), mediaLibrary.getUrl(), mediaLibrary.getUsername(), mediaLibrary.getPassword());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .build();

        Headers okhttpHeader = Headers.of(
                "Authorization", token
        );

        // refresh parent path
        Map<String, Object> data = Maps.newHashMap();
        data.put("path",  parentPath.substring(0, parentPath.lastIndexOf("/")));
        data.put("refresh", true);
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(data));
        Request request = new Request.Builder()
                .url(mediaLibrary.getUrl() + "/api/fs/list")
                .method("POST", requestBody)
                .headers(okhttpHeader)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data = Maps.newHashMap();
        data.put("path",  parentPath);
        data.put("refresh", true);
        requestBody = RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(data));
        request = new Request.Builder()
                .url(mediaLibrary.getUrl() + "/api/fs/list")
                .method("POST", requestBody)
                .headers(okhttpHeader)
                .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String responseStr = response.body().string();
        ListResponse listResponse = objectMapper.readValue(responseStr, ListResponse.class);
        if (Objects.isNull(listResponse.getData().getContent())) {
            return;
        }

        Set<String> extNameSet = Sets.newHashSet("mp4", "mkv");

        List<ListResponse.DataContent> files = listResponse.getData().getContent();
        files.forEach(file -> {
            String name = file.getName();
            if (!file.getIsDir()) {
                // 文件
                String filePath = parentPath + "/" + name;
                if (!extNameSet.contains(FileUtil.extName(new File(filePath)))) {
                    return;
                }
                filePaths.add(filePath);
            } else {
                // 目录
                getFileList(mediaLibrary, parentPath + "/" + name, filePaths);
            }
        });
    }

    private String getToken(Long libraryId, String url, String username, String password) {
        String token = (String) redisTemplate.opsForValue().get("media:library:token::" + libraryId);
        if ((StringUtils.isBlank(token))) {
            token = refreshToken(libraryId, url, username, password);
        } else {
            Mapper alistUserInfo = getAlistUserInfo(url, token);
            if (Objects.isNull(alistUserInfo)) {
                token = refreshToken(libraryId, url, username, password);
            }
        }
        return token;
    }


    private String refreshToken(Long libraryId, String url, String username, String password) {
        HTTP http = HTTP.builder()
                .baseUrl(url)
                .addMsgConvertor(new JacksonMsgConvertor())
                .build();

        HttpResult response = http.sync("/api/auth/login")
                .addBodyPara("Username", username)
                .addBodyPara("Password", password)
                .addBodyPara("opt_code", "")
                .post();

        Mapper mapper = response.getBody().toMapper();
        String token = mapper.getMapper("data").getString("token");
        redisTemplate.opsForValue().set("media:library:token::" + libraryId, token, 5, TimeUnit.MINUTES);

        return token;
    }

    private Mapper getAlistUserInfo(String url, String token) {
        HTTP http = HTTP.builder()
                .baseUrl(url)
                .addMsgConvertor(new JacksonMsgConvertor())
                .build();

        HttpResult response = http.sync("/api/me")
                .addHeader("Authorization", token)
                .get();

        Mapper mapper = response.getBody().toMapper();
        String code = mapper.getString("code");
        if ("200".equals(code)) {
            return mapper;
        }
        return null;
    }
}
