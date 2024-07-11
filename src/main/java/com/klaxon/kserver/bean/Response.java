package com.klaxon.kserver.bean;

import com.klaxon.kserver.exception.BizCodeEnum;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;

public class Response<T> {

	private String code;
	private String message;
	private Date timestamp = new Date();
	private T result;

	private Response() {
	}

	private Response(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	private Response(final BizCodeEnum bizCodeEnum) {
		this.code = bizCodeEnum.getCode();
		this.message = bizCodeEnum.getDesc();
	}

	private Response(final BizCodeEnum bizCodeEnum, final T result) {
		this(bizCodeEnum);
		this.result = result;
	}

	public static <R> Response<R> success() {
		return new Response<>(BizCodeEnum.COMMON_SUCCESS);
	}

	public static <R> Response<R> success(final String desc) {
		return new Response<>(BizCodeEnum.COMMON_SUCCESS.getCode(), desc);
	}

	public static <R> Response<R> success(R data) {
		return new Response<>(BizCodeEnum.COMMON_SUCCESS, data);
	}

	public static <R> Response<R> error() {
		return new Response<>(BizCodeEnum.COMMON_ERROR);
	}

	public static <R> Response<R> error(final String code) {
		return new Response<>(code, Strings.EMPTY);
	}

	public static <R> Response<R> error(final String code, final String desc) {
		return new Response<>(code, desc);
	}

	public static <R> Response<R> error(final BizCodeEnum bizCodeEnum) {
		return new Response<>(bizCodeEnum);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


}