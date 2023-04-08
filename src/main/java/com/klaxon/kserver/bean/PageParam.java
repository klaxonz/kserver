package com.klaxon.kserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;

public class PageParam {

	@TableField(exist = false)
	private int page;

	@TableField(exist = false)
	private int pageSize;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
