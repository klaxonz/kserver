package com.klaxon.kserver.converter;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.klaxon.kserver.controller.vo.AccountVo;
import com.klaxon.kserver.mapper.model.Account;
import com.klaxon.kserver.service.dto.AccountDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapperStruct {

	AccountVo entityToVo(Account account);

	AccountDto voToDto(AccountVo accountVo);

}
