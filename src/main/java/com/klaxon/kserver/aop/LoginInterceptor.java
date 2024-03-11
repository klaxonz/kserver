package com.klaxon.kserver.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.module.account.model.dto.AccountDto;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String token = request.getHeader("authorization");
		AccountDto accountVo = null;
		if (StringUtils.isNotBlank(token)) {
			String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
			Object accountObj = redisTemplate.opsForValue().get(key);
			if (accountObj != null) {
				accountVo = objectMapper.convertValue(accountObj, AccountDto.class);
			}
		}
		if (Objects.isNull(token) || accountVo == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
			return false;
		} else {
			OnlineUser onlineUser = new OnlineUser(accountVo.getId(), accountVo.getUsername());
			ThreadLocalHolder.setUser(onlineUser);
			return true;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ThreadLocalHolder.removeUser();
	}
}
