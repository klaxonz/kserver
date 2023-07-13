package com.klaxon.kserver.service.dto;

import com.klaxon.kserver.mapper.model.WebPage;

public class WebPageDto extends WebPage {

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
