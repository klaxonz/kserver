package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "t_tag")
public class Tag {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String tagName;
    private Timestamp updateTime;
    private Timestamp createTime;

}
