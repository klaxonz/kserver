package com.klaxon.kserver.controller.vo;


public class WebPageDetail {

	private Integer type;
	private Integer count;

	public WebPageDetail(Integer type, Integer count) {
		this.type = type;
		this.count = count;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
