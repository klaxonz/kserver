package com.klaxon.kserver.aop;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;

@RestControllerAdvice
public class ResultResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(@NotNull MethodParameter returnType,
			@NotNull Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod() != null;
	}

	@Override
	public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType,
			@NotNull MediaType selectedContentType,
			@NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
			@NotNull ServerHttpRequest request,
			@NotNull ServerHttpResponse response) {
		if (body == null || body instanceof Response) {
			return body;
		}

		Response<Object> result = Response.success(body);
		if (body instanceof String) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.writeValueAsString(result);
			} catch (JsonProcessingException e) {
				throw new BizException(BizCodeEnum.COMMON_ERROR, e);
			}
		}
		return result;
	}
}
