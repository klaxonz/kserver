package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "t_account")
public class Account {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Timestamp updateTime;
    private Timestamp createTime;

}
