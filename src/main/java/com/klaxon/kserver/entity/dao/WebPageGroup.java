package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;


@Data
@TableName(value = "t_webpage_group")
public class WebPageGroup {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long webpageId;
    private Long groupId;
    private Timestamp updateTime;
    private Timestamp createTime;

}
