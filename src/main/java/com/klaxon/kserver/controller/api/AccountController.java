package com.klaxon.kserver.controller.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.controller.vo.AccountVo;
import com.klaxon.kserver.converter.AccountMapperStruct;
import com.klaxon.kserver.service.AccountService;
import com.klaxon.kserver.service.dto.AccountDto;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Resource
	private AccountService accountService;
	@Resource
	private AccountMapperStruct accountMapperStruct;

	@PostMapping("/login")
	public Response<Object> login(@RequestBody AccountVo accountVo, HttpServletResponse response) {
		AccountDto accountDto = accountMapperStruct.voToDto(accountVo);
		String token = accountService.login(accountDto);
		response.setHeader("authorization", token);
		return Response.success();
	}

	@PostMapping("/logout")
	public Response<Object> logout(HttpServletRequest request) {
		accountService.logout(request);
		return Response.success();
	}

	@PostMapping("/register")
	public Response<Object> register(@RequestBody AccountVo accountVo) {
		AccountDto accountDto = accountMapperStruct.voToDto(accountVo);
		accountService.createAccount(accountDto);
		return Response.success();
	}

}
