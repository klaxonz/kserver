package com.klaxon.kserver.module.media.model.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class MediaLibrarySyncReq {

    @NotEmpty(message = "libraryIds不能为空")
    private List<Long> libraryIds;

}
