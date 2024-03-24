package com.klaxon.kserver.module.media.extractor;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class MediaExtractorFactory {

    public static FileExtractInfo parse(List<FileExtractInfo> extractInfos) {
        FileExtractInfo extractTv = findByType(extractInfos, FileExtractInfo.TYPE_TV);
        FileExtractInfo extractMovie = findByType(extractInfos, FileExtractInfo.TYPE_MOVIE);

        if (Objects.nonNull(extractMovie) && StringUtils.isNotBlank(extractMovie.getName()) && Objects.isNull(extractMovie.getSeason())) {
            return extractMovie;
        }
        if (Objects.nonNull(extractTv) && StringUtils.isNotBlank(extractTv.getName()) && Objects.nonNull(extractTv.getEpisode())) {
            return extractTv;
        }
        return null;
    }

    private static FileExtractInfo findByType(List<FileExtractInfo> extractInfos, Integer type) {
        for (FileExtractInfo extractInfo : extractInfos) {
            if (extractInfo.getType().equals(type)) {
                return extractInfo;
            }
        }
        return null;
    }

}
