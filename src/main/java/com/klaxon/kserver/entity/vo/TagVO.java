package com.klaxon.kserver.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TagVO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String tagName;

}
