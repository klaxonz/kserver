package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;


@Data
@TableName(value = "t_webpage")
public class WebPage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long groupId;
    private String url;
    private String title;
    private String source;
    private String isStar;
    private String favicon;
    private String description;
    private Timestamp updateTime;
    private Timestamp createTime;

}
