package com.klaxon.kserver.module.account.controller;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.module.account.model.req.AccountAddReq;
import com.klaxon.kserver.module.account.model.req.AccountLoginReq;
import com.klaxon.kserver.module.account.service.AccountService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "账户管理")
@RestController
@RequestMapping("/account")
public class AccountController {

	@Resource
	private AccountService accountService;

	@PostMapping("/login")
	public Response<Object> login(@RequestBody AccountLoginReq req, HttpServletResponse response) {
		String token = accountService.login(req);
		response.setHeader("authorization", token);
		return Response.success();
	}

	@PostMapping("/logout")
	public Response<Object> logout(HttpServletRequest request) {
		accountService.logout(request);
		return Response.success();
	}

	@PostMapping("/register")
	public Response<Object> register(@RequestBody AccountAddReq req) {
		accountService.addAccount(req);
		return Response.success();
	}

}
