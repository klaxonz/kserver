package com.klaxon.kserver.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.downloader.YtDlpDownloadProgress;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;

import cn.hutool.core.util.RandomUtil;


@Component
public class RedisUpdateAndAddListener implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(RedisUpdateAndAddListener.class);

    // 监听的主题
    private  final PatternTopic topic = new PatternTopic("__keyevent@*__:set");

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private WebPageTaskMapper webPageTaskMapper;
    @Resource
    private WebPageVideoTaskMapper webPageVideoTaskMapper;

    public PatternTopic getTopic() {
        return topic;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public WebPageTaskMapper getWebPageTaskMapper() {
        return webPageTaskMapper;
    }

    public void setWebPageTaskMapper(WebPageTaskMapper webPageTaskMapper) {
        this.webPageTaskMapper = webPageTaskMapper;
    }

    public WebPageVideoTaskMapper getWebPageVideoTaskMapper() {
        return webPageVideoTaskMapper;
    }

    public void setWebPageVideoTaskMapper(WebPageVideoTaskMapper webPageVideoTaskMapper) {
        this.webPageVideoTaskMapper = webPageVideoTaskMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern){
        String msg = new String(message.getBody());
        if (!msg.startsWith("task:progress:")) {
            return;
        }

        int i = RandomUtil.randomInt(10);
        if (i % 5 != 0) {
            return;
        }

        String progressJson = redisTemplate.opsForValue().get(msg);
        YtDlpDownloadProgress progress = null;
        try {
            progress = objectMapper.readValue(progressJson, YtDlpDownloadProgress.class);
        } catch (JsonProcessingException e) {
            log.error("下载进度Json反序列化异常", e);
        }
        log.debug("进度消息, {}", progressJson);

        if (progress != null) {
            Integer taskId = Integer.valueOf(msg.split("task:progress:")[1]);
            WebPageVideoTask webPageVideoTask = webPageVideoTaskMapper.selectById(taskId);

            String type = progress.getType();
            if (type.equals("video") || type.equals("best")) {
                webPageVideoTask.setVideoProgress(progress.getPercent());
                webPageVideoTask.setVideoLength(progress.getTotalBytes());
                webPageVideoTask.setVideoDownloadedLength(progress.getDownloadedBytes());
                webPageVideoTask.setVideoPath(progress.getFilepath());
            }
            if (type.equals("audio")) {
                webPageVideoTask.setAudioProgress(progress.getPercent());
                webPageVideoTask.setAudioLength(progress.getTotalBytes());
                webPageVideoTask.setAudioDownloadedLength(progress.getDownloadedBytes());
                webPageVideoTask.setAudioPath(progress.getFilepath());
            }
            webPageVideoTaskMapper.updateById(webPageVideoTask);

            WebPageTask task = webPageTaskMapper.selectById(webPageVideoTask.getTaskId());
            Long videoDownloadedSize = webPageVideoTaskMapper.queryVideoDownloadedSize(task.getId());
            task.setVideoDownloadedSize(videoDownloadedSize);
            Long videoSize = task.getVideoSize();
            Integer videoDownloadProgress = new BigDecimal(String.valueOf(videoDownloadedSize))
                    .divide(new BigDecimal(String.valueOf(videoSize)), 6, RoundingMode.CEILING)
                    .multiply(new BigDecimal("10000")).intValue();
            task.setVideoProgress(videoDownloadProgress);
            webPageTaskMapper.updateById(task);
        }
    }

}