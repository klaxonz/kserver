package com.klaxon.kserver.module.media.extractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileExtractInfo {

    public static final Integer TYPE_TV = 1;
    public static final Integer TYPE_MOVIE = 2;

    private Integer type;
    private String name;
    private Integer year;
    private Integer season;
    private Integer episode;
    private String filepath;

}
