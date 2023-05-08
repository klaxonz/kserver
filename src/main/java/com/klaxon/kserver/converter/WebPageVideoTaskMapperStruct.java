package com.klaxon.kserver.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.klaxon.kserver.controller.vo.WebPageVideoTaskVo;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.service.dto.WebPageVideoTaskDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebPageVideoTaskMapperStruct {

	WebPageVideoTaskDto voToDto(WebPageVideoTaskVo webPageVideoTaskVo);

	WebPageVideoTaskVo dtoToVo(WebPageVideoTaskDto webPageVideoTaskDto);

	WebPageVideoTaskDto entityToDto(WebPageVideoTask webPageVideoTask);

	List<WebPageVideoTaskDto> entitiesToDtos(List<WebPageVideoTask> webPageVideoTasks);

	List<WebPageVideoTaskVo> dtosToVos(List<WebPageVideoTaskDto> webPageVideoTaskDtos);
}
