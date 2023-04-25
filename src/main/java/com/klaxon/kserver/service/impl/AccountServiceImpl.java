package com.klaxon.kserver.service.impl;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.controller.vo.AccountVo;
import com.klaxon.kserver.converter.AccountMapperStruct;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.mapper.AccountMapper;
import com.klaxon.kserver.mapper.model.Account;
import com.klaxon.kserver.service.AccountService;
import com.klaxon.kserver.service.dto.AccountDto;

import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	@Resource
	private AccountMapper accountMapper;
	@Resource
	private AccountMapperStruct accountMapperStruct;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void createAccount(AccountDto accountDto) {

		// 用户名重复校验
		Account usernameAccount = accountMapper.selectOne(
				new LambdaQueryWrapper<Account>().eq(Account::getUsername, accountDto.getUsername()));
		if (!Objects.isNull(usernameAccount)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030001);
		}
		// 邮箱重复校验
		Account emailAccount = accountMapper
				.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, accountDto.getEmail()));
		if (!Objects.isNull(emailAccount)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030004);
		}

		// md5 加密密码
		MD5 md5 = MD5.create();
		String digestHex = md5.digestHex(accountDto.getPassword());

		Account account = new Account();
		account.setUsername(account.getUsername());
		account.setPassword(digestHex);
		account.setEmail(account.getEmail());
		accountMapper.insert(account);
	}

	@Override
	public String login(AccountDto accountDto) {
		Account account = accountMapper
				.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, accountDto.getEmail()));
		if (Objects.isNull(account)) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030002);
		}
		MD5 md5 = MD5.create();
		String digestHex = md5.digestHex(accountDto.getPassword());
		if (!StringUtils.equals(digestHex, account.getPassword())) {
			throw new BizException(BizCodeEnum.ACCOUNT_0030003);
		}

		AccountVo accountVo = accountMapperStruct.entityToVo(account);

		String token = UUID.randomUUID().toString().replace("-", "");
		String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
		redisTemplate.opsForValue().set(key, accountVo);

		return token;
	}

	@Override
	public void logout(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
		redisTemplate.opsForValue().getAndExpire(key, 1, TimeUnit.MICROSECONDS);
		request.getSession().invalidate();
	}

}
