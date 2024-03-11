package com.klaxon.kserver.module.account.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.account.mapper.AccountMapper;
import com.klaxon.kserver.module.account.model.dto.AccountDto;
import com.klaxon.kserver.module.account.model.entity.Account;
import com.klaxon.kserver.module.account.model.req.AccountAddReq;
import com.klaxon.kserver.module.account.model.req.AccountLoginReq;
import com.klaxon.kserver.module.account.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl implements AccountService {

	@Resource
	private AccountMapper accountMapper;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void addAccount(AccountAddReq req) {
		checkAddReq(req);

		// md5 加密密码
		MD5 md5 = MD5.create();
		String digestHex = md5.digestHex(req.getPassword());

		Account account = new Account();
		account.setUsername(req.getUsername());
		account.setPassword(digestHex);
		account.setEmail(req.getEmail());
		accountMapper.insert(account);
	}

	private void checkAddReq(AccountAddReq req) {
		String username = req.getUsername();
		if (Objects.isNull(username)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030005);
		}

		// 用户名重复校验
		Account usernameAccount = accountMapper.selectOne(
				new LambdaQueryWrapper<Account>().eq(Account::getUsername, req.getUsername()));
		if (!Objects.isNull(usernameAccount)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030001);
		}
		// 邮箱重复校验
		Account emailAccount = accountMapper
				.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, req.getEmail()));
		if (!Objects.isNull(emailAccount)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030004);
		}
	}

	@Override
	public String login(AccountLoginReq req) {
		Account account = checkLoginReq(req);

		AccountDto accountDto = new AccountDto();
		accountDto.setId(account.getId());
		accountDto.setUsername(account.getUsername());
		accountDto.setEmail(account.getEmail());

		String token = UUID.randomUUID().toString().replace("-", "");
		String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
		redisTemplate.opsForValue().set(key, accountDto);

		return token;
	}

	private Account checkLoginReq(AccountLoginReq req) {
		Account account = accountMapper
				.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, req.getEmail()));
		if (Objects.isNull(account)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030002);
		}
		MD5 md5 = MD5.create();
		String digestHex = md5.digestHex(req.getPassword());
		if (!StringUtils.equals(digestHex, account.getPassword())) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030003);
		}
		return account;
	}

	@Override
	public void logout(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
		redisTemplate.opsForValue().getAndExpire(key, 1, TimeUnit.MICROSECONDS);
		request.getSession().invalidate();
	}

}
