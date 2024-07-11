package com.klaxon.kserver.module.media.subscribe.provider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieItem {
    private String title;
    private Integer release;
    // 0 已上映 1 正在上映 2 即将上映
    private Integer type;
}