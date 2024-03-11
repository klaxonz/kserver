package com.klaxon.kserver.module.webpage.model.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

/**
 * @author klaxon
 */
@ApiModel
public class WebPageAddReq {

	@ApiModelProperty("链接url")
	@NotBlank(message = "URL不能为空")
	@URL(message = "URL格式错误")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
