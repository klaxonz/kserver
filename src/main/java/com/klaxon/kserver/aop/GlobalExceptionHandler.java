package com.klaxon.kserver.aop;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = Exception.class)
	public <R> Response<R> exceptionHandler(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return Response.error();
	}

	@ExceptionHandler(value = BizException.class)
	public <R> Response<R> bizExceptionHandler(BizException ex) {
		logger.error(ex.getMessage(), ex);
		return Response.error(ex.getCode(), ex.getDesc());
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public <R> Response<R> validateExceptionHandler(MethodArgumentNotValidException argumentNotValidException) {
		logger.error(argumentNotValidException.getMessage(), argumentNotValidException);
		String message = argumentNotValidException.getBindingResult().getAllErrors()
				.stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(","));
		return Response.error(BizCodeEnum.COMMON_ARGUMENT_ERROR.getCode(), message);
	}

	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public <R> Response<R> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
		logger.error(ex.getMessage(), ex);
		return Response.error(BizCodeEnum.COMMON_HTTP_METHOD_NOT_SUPPORT);
	}

}
