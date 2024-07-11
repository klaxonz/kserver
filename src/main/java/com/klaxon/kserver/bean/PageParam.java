package com.klaxon.kserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageParam {

	@TableField(exist = false)
	private int page = 1;

	@TableField(exist = false)
	private int pageSize = 10;

}
