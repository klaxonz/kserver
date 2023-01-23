package com.klaxon.kserver.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AccountVo {

    private Long id;
    private String username;
    private String email;
    private Timestamp updateTime;
    private Timestamp createTime;

}
