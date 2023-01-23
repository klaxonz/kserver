package com.klaxon.kserver.mapperstruct;

import com.klaxon.kserver.entity.dto.WebPageDto;
import com.klaxon.kserver.entity.vo.WebPageVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebPageMapperStruct {

    WebPageDto voToDto(WebPageVo account);

}
