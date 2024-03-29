package com.klaxon.kserver.exception;

public enum BizCodeEnum {

	/**
	 * 异常码由 7 位构成
	 * 前 3 位按业务划分 001 ~ 999
	 * 后 4 位按具体业务划分 0000 ~ 9999
	 * 通用异常码前 3 位为 000
	 * 成功的异常码为 0000000
	 */
	COMMON_SUCCESS("0000000", "成功"),
	COMMON_ARGUMENT_ERROR("0000001", "参数异常"),
	COMMON_HTTP_METHOD_NOT_SUPPORT("0000002", "接口资源不存在"),
	RESOURCE_EXIST("0000003", "资源已存在"),
	RESOURCE_NOT_EXIST("0000004", "资源不存在"),
	COMMON_ERROR("0009999", "系统未知异常，请稍后再试"),
	WEBPAGE_0010001("0010001", "资源不存在"),
	GROUP_0020001("0020001", "分组名称不能为空"),
	GROUP_0020002("0020001", "分组不存在"),
	ACCOUNT_0030001("0030001", "用户名已存在"),
	ACCOUNT_0030002("0030002", "用户不存在"),
	ACCOUNT_0030003("0030003", "账号或密码错误"),
	ACCOUNT_0030004("0030004", "邮箱已存在"),
	ACCOUNT_0030005("0030005", "用户名不能为空"),
	MEDIA_LIBRARY_SYNC_ERROR("0031001", "媒体库同步异常")
	;

	private final String code;
	private final String desc;

	BizCodeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
