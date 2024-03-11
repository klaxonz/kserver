package com.klaxon.kserver.module.account.service;

import com.klaxon.kserver.module.account.model.req.AccountAddReq;
import com.klaxon.kserver.module.account.model.req.AccountLoginReq;

import javax.servlet.http.HttpServletRequest;

public interface AccountService {

	void addAccount(AccountAddReq req);

	String login(AccountLoginReq req);

	void logout(HttpServletRequest request);

}
