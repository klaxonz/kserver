package com.klaxon.kserver.module.media.task;

import com.google.common.collect.Lists;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.media.extractor.FileExtractInfo;
import com.klaxon.kserver.module.media.extractor.MediaExtractorFactory;
import com.klaxon.kserver.module.media.extractor.MediaMetaExtractor;
import com.klaxon.kserver.module.media.extractor.MediaMetaExtractorHandler;
import com.klaxon.kserver.module.media.extractor.MovieExtractor;
import com.klaxon.kserver.module.media.extractor.TvExtractor;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class MediaMetaAsyncTask {

    @Resource
    private MediaMetaExtractorHandler mediaMetaExtractorHandler;

    @Async
    public void doTask(String filePath, MediaLibrary mediaLibrary) {
        // 解析文件名，判断是电影还是电视剧
        FileExtractInfo extractTv = new TvExtractor().extract(filePath);
        FileExtractInfo extractMovie = new MovieExtractor().extract(filePath);
        FileExtractInfo extractInfo = MediaExtractorFactory.parse(Lists.newArrayList(extractMovie, extractTv));
        if (Objects.isNull(extractInfo)) {
            log.info("Extract file type failed, filepath: {}", filePath);
            return;
        }
        try {
            MediaMetaExtractor mediaMetaExtractor = mediaMetaExtractorHandler.getMediaMetaExtractors(extractInfo.getType());
            mediaMetaExtractor.extract(mediaLibrary, extractInfo);
        } catch (Exception e) {
            throw new BizException(BizCodeEnum.MEDIA_LIBRARY_SYNC_ERROR, e);
        }
    }

}
