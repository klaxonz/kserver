package com.klaxon.kserver.module.media.extractor;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieExtractor implements MediaExtractor {

    @Override
    public FileExtractInfo extract(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String filename = FileUtil.mainName(new File(path));
        String[] parts = filename.split("\\.");
        Pattern yearRegexp = Pattern.compile("(?i)(19|20)\\d{2}");
        String yearPart = "";
        int yearIndex = 0;

        for (int i = 0; i < parts.length; i++) {
            Matcher matcher = yearRegexp.matcher(parts[i]);
            if (matcher.find()) {
                yearPart = matcher.group();
                yearIndex = i;
                break;
            }
        }

        if (yearPart.isEmpty()) {
            yearIndex = parts.length;
        }

        List<String> nameParts = new ArrayList<>();
        for (int i = 0; i < yearIndex; i++) {
            nameParts.add(parts[i]);
        }

        List<String> englishNameParts = new ArrayList<>();
        List<String> chineseNameParts = new ArrayList<>();
        for (String part : nameParts) {
            if (isChineseCharacters(part)) {
                chineseNameParts.add(part);
            } else {
                englishNameParts.add(part.replace(".", " "));
            }
        }

        String name = "";
        if (!englishNameParts.isEmpty()) {
            name = String.join(" ", englishNameParts);
        } else if (!chineseNameParts.isEmpty()) {
            name = String.join("", chineseNameParts);
        }

        return FileExtractInfo.builder()
                .type(FileExtractInfo.TYPE_MOVIE)
                .name(name)
                .year(StringUtils.isNotBlank(yearPart) ? Integer.valueOf(yearPart) : null)
                .filepath(path)
                .build();
    }

    private boolean isChineseCharacters(String str) {
        for (char r : str.toCharArray()) {
            if (Character.UnicodeScript.of(r) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

}
