package com.klaxon.kserver.service.dto;

import java.io.Serializable;

import com.klaxon.kserver.mapper.model.WebPageVideoTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebPageVideoTaskDto extends WebPageVideoTask implements Serializable {

	private static final long serialVersionUID = -6870274880984941595L;

}
