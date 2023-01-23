package com.klaxon.kserver.controller;

import com.klaxon.kserver.constants.AccountConstant;
import com.klaxon.kserver.entity.dto.LoginAccountDto;
import com.klaxon.kserver.entity.dto.RegisterAccountDto;
import com.klaxon.kserver.entity.vo.AccountVo;
import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController {


    @Autowired
    private IAccountService accountService;

    @PostMapping("/login")
    public Response<Object> login(@RequestBody LoginAccountDto loginAccountDto, HttpServletRequest request) {
        AccountVo accountVo = accountService.login(loginAccountDto);
        request.getSession().setAttribute(AccountConstant.SESSION_NAME, accountVo);
        return Response.success(accountVo);
    }

    @PostMapping("/logout")
    public Response<Object> logout(HttpServletRequest request) {
        accountService.logout(request);
        return Response.success();
    }

    @PostMapping("/register")
    public Response<Object> register(@RequestBody RegisterAccountDto registerAccountDto) {
        accountService.addAccount(registerAccountDto);
        return Response.success();
    }

}
