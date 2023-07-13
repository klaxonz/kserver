package com.klaxon.kserver.controller.vo;

import com.klaxon.kserver.mapper.model.WebPage;

public class WebPageVo extends WebPage {

	private String type;
	private String query;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
