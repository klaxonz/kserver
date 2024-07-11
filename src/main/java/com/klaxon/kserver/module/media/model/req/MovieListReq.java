package com.klaxon.kserver.module.media.model.req;

import com.klaxon.kserver.bean.PageParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieListReq extends PageParam {

    private String name;

}
