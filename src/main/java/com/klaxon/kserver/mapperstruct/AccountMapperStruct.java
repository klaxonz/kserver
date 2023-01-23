package com.klaxon.kserver.mapperstruct;

import com.klaxon.kserver.entity.dao.Account;
import com.klaxon.kserver.entity.vo.AccountVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapperStruct {

    AccountVo entityToVo(Account account);

}
