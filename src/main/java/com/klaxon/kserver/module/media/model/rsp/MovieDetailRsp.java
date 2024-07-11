package com.klaxon.kserver.module.media.model.rsp;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MovieDetailRsp {

    private Long id;
    private String name;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private LocalDate release;
    private List<String> genres;
    private List<MovieCastRsp> casts;
    private String url;

    @Getter
    @Setter
    public static class MovieCastRsp {
        private String name;
        private String character;
        private String profilePath;
    }

}
