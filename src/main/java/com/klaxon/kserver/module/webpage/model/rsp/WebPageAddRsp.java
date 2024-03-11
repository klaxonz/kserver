package com.klaxon.kserver.module.webpage.model.rsp;

import com.klaxon.kserver.module.webpage.model.entity.WebPage;

/**
 * @author klaxon
 */
public class WebPageAddRsp extends WebPage {

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
