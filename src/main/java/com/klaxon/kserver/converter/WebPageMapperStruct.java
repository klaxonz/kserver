package com.klaxon.kserver.converter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.bean.BasePage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.klaxon.kserver.controller.vo.WebPageVo;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.service.dto.WebPageDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebPageMapperStruct {

	WebPageDto voToDto(WebPageVo request);

	WebPageDto entityToDto(WebPage webPage);

	WebPageVo dtoToVo(WebPageDto webPageDto);

	BasePage<WebPageVo> convertVoPage(IPage<WebPageVo> webPageVoIPage);

	BasePage<WebPageDto> convertDtoPage(IPage<WebPageDto> webPageDtoIPage);

}
