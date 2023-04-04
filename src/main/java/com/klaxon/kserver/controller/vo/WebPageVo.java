package com.klaxon.kserver.controller.vo;

import com.klaxon.kserver.mapper.model.WebPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebPageVo extends WebPage {

	private String type;

}
