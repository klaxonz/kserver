package com.klaxon.kserver.module.media.model.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MovieDetailReq {

    @NotNull(message = "id不能为空")
    private Long id;

}
