package com.klaxon.kserver.module.media.model.rsp;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MovieListRsp {

    private Long id;
    private String name;
    private LocalDate release;
    private String poster;

}
