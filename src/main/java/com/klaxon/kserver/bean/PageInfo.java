package com.klaxon.kserver.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "通用返回分页结果对象", value = "result")
public class PageInfo<T> implements Serializable {
    //当前页码
    @ApiModelProperty(notes = "当前页", required = true)
    private Long page;
    //每页显示行
    @ApiModelProperty(notes = "每页显示的条数", required = true)
    private Long size;
    //总记录数
    @ApiModelProperty(notes = "总记录数", required = true)
    private Long total;
    //总页数
    @ApiModelProperty(notes = "总页数", required = true)
    private Long totalPages;
    //当前页记录
    @ApiModelProperty(notes = "响应的数据结果集", required = false)
    private List<T> list;
}
