package com.klaxon.kserver.module.webpage.service;


import com.klaxon.kserver.module.webpage.model.req.WebPageAddReq;

/**
 * @author klaxon
 */
public interface WebPageService {

	/**
	 * 保存网页
	 * @param req	req
	 * @return	网页信息
	 */
	void add(WebPageAddReq req);


}
