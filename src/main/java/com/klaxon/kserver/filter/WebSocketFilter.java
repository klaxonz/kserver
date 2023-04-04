package com.klaxon.kserver.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.controller.vo.AccountVo;
import com.klaxon.kserver.util.ThreadLocalHolder;

@WebFilter("/ws/*")
public class WebSocketFilter implements Filter {

	private ObjectMapper objectMapper;
	private RedisTemplate redisTemplate;

	@Override
	public void init(FilterConfig filterConfig) {
		ServletContext servletContext = filterConfig.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		assert applicationContext != null;
		objectMapper = applicationContext.getBean(ObjectMapper.class);
		redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String token = req.getParameter("token");

		AccountVo accountVo = null;
		if (StringUtils.isNotBlank(token)) {
			String key = "account:token:" + token;
			Map<String, Object> userInfo = (Map<String, Object>) redisTemplate.opsForValue().get(key);
			if (userInfo != null) {
				accountVo = objectMapper.convertValue(userInfo, AccountVo.class);
			}
		}
		if (Objects.isNull(token) || accountVo == null) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			return;
		} else {
			OnlineUser onlineUser = new OnlineUser(accountVo.getId(), accountVo.getUsername());
			ThreadLocalHolder.setUser(onlineUser);
		}

		chain.doFilter(request, response);
	}
}
