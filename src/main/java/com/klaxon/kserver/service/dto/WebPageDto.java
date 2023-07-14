package com.klaxon.kserver.service.dto;

import com.klaxon.kserver.mapper.model.WebPage;

public class WebPageDto extends WebPage {

	private Integer type;
	private String query;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
