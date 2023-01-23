package com.klaxon.kserver.entity.dto;


import lombok.Data;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.NotBlank;

@Data
public class WebPageDto {

    private Long groupId;
    @URL
    @NotBlank(message = "资源路径不能为空")
    private String url;
    private String source;

}
