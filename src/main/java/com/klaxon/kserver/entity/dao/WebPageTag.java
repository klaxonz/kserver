package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "t_webpage_tag")
public class WebPageTag {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long webpageId;
    private Long tagId;
    private Timestamp updateTime;
    private Timestamp createTime;

}
