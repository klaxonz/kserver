package com.klaxon.kserver.controller.api;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.mapper.VideoMapper;
import com.klaxon.kserver.mapper.model.Video;
import com.klaxon.kserver.property.VideoProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Resource
    private VideoProperty videoProperty;
    @Resource
    private VideoMapper videoMapper;

    @PostMapping("/upload")
    public Response<Object> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();

            Video video = new Video();
            video.setVideoPath(fileName);
            video.setTitle(fileName);
            video.setIsMulti(0);
            video.setVideoIndex(0);

            // 在指定目录创建文件
            File dest = new File(videoProperty.getVideoStorePath() + fileName);
            file.transferTo(dest);

            videoMapper.insert(video);

        } catch (IOException e) {
            log.error("store video error", e);
        }
        return Response.success();
    }

}
