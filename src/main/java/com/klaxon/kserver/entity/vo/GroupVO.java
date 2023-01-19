package com.klaxon.kserver.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GroupVO {

    private Long id;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

}
