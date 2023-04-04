package com.klaxon.kserver.mapper.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klaxon.kserver.bean.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value = "t_account")
@EqualsAndHashCode(callSuper = true)
public class Account extends PageParam implements Serializable {

	private static final long serialVersionUID = 6556005622975089942L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	private String username;
	private String password;
	private String email;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
