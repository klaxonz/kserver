package com.klaxon.kserver.module.media.model.req;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class MediaLibraryMountReq {

    @NotNull(message = "libraryId不能为空")
    private Long libraryId;
    @NotEmpty(message = "挂载路径不能为空")
    private List<String> paths;

}
