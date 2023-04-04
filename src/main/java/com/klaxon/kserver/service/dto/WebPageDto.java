package com.klaxon.kserver.service.dto;

import com.klaxon.kserver.mapper.model.WebPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebPageDto extends WebPage {

	private String type;

}
