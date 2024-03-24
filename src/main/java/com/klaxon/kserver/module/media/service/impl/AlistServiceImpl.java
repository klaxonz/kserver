package com.klaxon.kserver.module.media.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.zhxu.data.Array;
import cn.zhxu.data.Mapper;
import cn.zhxu.data.jackson.JacksonMap;
import cn.zhxu.okhttps.HTTP;
import cn.zhxu.okhttps.HttpResult;
import cn.zhxu.okhttps.jackson.JacksonMsgConvertor;
import com.google.common.collect.Sets;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.service.AlistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AlistServiceImpl implements AlistService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void getFileList(MediaLibrary mediaLibrary, String parentPath, List<String> filePaths) {
        HTTP http = HTTP.builder()
                .baseUrl(mediaLibrary.getUrl())
                .addMsgConvertor(new JacksonMsgConvertor())
                .build();

        String token = getToken(mediaLibrary.getId(), mediaLibrary.getUrl(), mediaLibrary.getUsername(), mediaLibrary.getPassword());
        HttpResult response = http.sync("/api/fs/list")
                .addHeader("Authorization", token)
                .addBodyPara("path", parentPath)
                .post();

        Mapper mapper = response.getBody().toMapper();
        Mapper data = mapper.getMapper("data");
        Array files = data.getArray("content");
        List<Object> list = files.toList();

        Set<String> extNameSet = Sets.newHashSet("mp4", "mkv");

        list.forEach(file -> {
            Boolean isDir = (Boolean) ((JacksonMap) file).get("is_dir");
            String name = (String) ((JacksonMap) file).get("name");
            if (!isDir) {
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
