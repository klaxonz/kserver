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
public class MediaLibraryAddReq {

    @NotBlank(message = "媒体库地址不能为空")
    @URL(message = "媒体库地址格式错误")
    private String url;

    @NotNull(message = "媒体库名称不能为空")
    private String name;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotEmpty(message = "目录不能为空")
    private List<String> paths;

}
