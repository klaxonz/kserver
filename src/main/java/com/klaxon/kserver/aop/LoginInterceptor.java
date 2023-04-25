package com.klaxon.kserver.aop;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.controller.vo.AccountVo;
import com.klaxon.kserver.util.ThreadLocalHolder;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
			throws Exception {
		String token = request.getHeader("authorization");
		AccountVo accountVo = null;
		if (StringUtils.isNotBlank(token)) {
			String key = RedisKeyPrefixConstants.ACCOUNT_TOKEN_PREFIX + token;
			Object accountObj = redisTemplate.opsForValue().get(key);
			if (accountObj != null) {
				accountVo = objectMapper.convertValue(accountObj, AccountVo.class);
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
	public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler, ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler, Exception ex) {
		ThreadLocalHolder.removeUser();
	}
}
