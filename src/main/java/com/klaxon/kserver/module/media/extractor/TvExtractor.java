package com.klaxon.kserver.module.media.extractor;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TvExtractor implements MediaExtractor {

    @Override
    public FileExtractInfo extract(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String filename = FileUtil.mainName(new File(path));

        String name = "";
        String year = "";
        String season = "";
        String episode = "";
        String seasonEpisode = "";

        // Match season and episode
        Pattern re = Pattern.compile("(?i)(S\\d{2}E\\d{2})");
        Matcher loc = re.matcher(filename);

        if (loc.find()) {
            // Extract season and episode information
            seasonEpisode = loc.group();

            // Extract year information
            String text = filename.substring(0, loc.start());
            Pattern yearReg = Pattern.compile("\\b\\d{4}\\b");
            Matcher yearLoc = yearReg.matcher(text);
            if (yearLoc.find()) {
                year = text.substring(yearLoc.start(), yearLoc.end());
                text = text.substring(0, yearLoc.start());
            } else {
                yearLoc = yearReg.matcher(filename);
                if (yearLoc.find()) {
                    year = filename.substring(yearLoc.start(), yearLoc.end());
                }
            }

            // Determine if there are Chinese and English characters
            boolean hasChinese = false;
            boolean hasEnglish = false;
            for (char ch : text.toCharArray()) {
                if (Character.UnicodeScript.of(ch) == Character.UnicodeScript.HAN) {
                    hasChinese = true;
                } else if (Character.isLetter(ch)) {
                    hasEnglish = true;
                }
            }

            // If both Chinese and English exist, remove Chinese
            if (hasChinese && hasEnglish) {
                text = text.replaceAll("[\\u4e00-\\u9fa5]+", "");
            }
            text = text.replace(".", " "); // Replace dots with spaces
            text = text.trim();
            name = text;
        }

        if (StringUtils.isNotBlank(seasonEpisode)) {
            Pattern sePattern = Pattern.compile("(?i)S(\\d{2})E(\\d{2})");
            Matcher seMatcher = sePattern.matcher(seasonEpisode);

            if (seMatcher.find() && seMatcher.groupCount() == 2) {
                season = seMatcher.group(1).replaceFirst("^0+(?!$)", "");
                episode = seMatcher.group(2).replaceFirst("^0+(?!$)", "");
            }
        }

        return FileExtractInfo.builder()
                .type(FileExtractInfo.TYPE_TV)
                .name(name)
                .year(StringUtils.isNotBlank(year) ? Integer.valueOf(year) : null)
                .season(StringUtils.isNotBlank(season) ? Integer.valueOf(season) : null)
                .episode(StringUtils.isNotBlank(episode) ? Integer.valueOf(episode) : null)
                .filepath(path)
                .build();
    }

}
