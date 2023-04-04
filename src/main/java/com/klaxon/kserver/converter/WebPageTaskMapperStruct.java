package com.klaxon.kserver.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.klaxon.kserver.controller.vo.WebPageTaskVo;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.service.dto.WebPageTaskDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebPageTaskMapperStruct {

	WebPageTaskDto voToDto(WebPageTaskVo webPageTaskVo);

	WebPageTaskVo dtoToVo(WebPageTaskDto webPageTaskDto);

	WebPageTaskDto entityToDto(WebPageTask webPageTask);

	List<WebPageTaskDto> entitiesToDtos(List<WebPageTask> webPageTasks);
}
