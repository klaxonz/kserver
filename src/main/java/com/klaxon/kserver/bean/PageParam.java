package com.klaxon.kserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

@Data
public class PageParam {

	@TableField(exist = false)
	private int page;
	@TableField(exist = false)
	private int pageSize;

}
