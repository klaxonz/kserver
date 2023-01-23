package com.klaxon.kserver.service;


import com.klaxon.kserver.entity.dto.LoginAccountDto;
import com.klaxon.kserver.entity.dto.RegisterAccountDto;
import com.klaxon.kserver.entity.vo.AccountVo;

import javax.servlet.http.HttpServletRequest;

public interface IAccountService {

    void addAccount(RegisterAccountDto registerAccountDto);

    AccountVo login(LoginAccountDto loginAccountDto);

    void logout(HttpServletRequest request);


}
