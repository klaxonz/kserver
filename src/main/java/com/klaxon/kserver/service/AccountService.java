package com.klaxon.kserver.service;

import javax.servlet.http.HttpServletRequest;

import com.klaxon.kserver.service.dto.AccountDto;

public interface AccountService {

	void createAccount(AccountDto accountDto);

	String login(AccountDto accountDto);

	void logout(HttpServletRequest request);

}
