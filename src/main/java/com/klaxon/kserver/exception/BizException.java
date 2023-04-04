package com.klaxon.kserver.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

	private final String code;
	private final String desc;

	public BizException(String code, String desc) {
		super(desc);
		this.code = code;
		this.desc = desc;
	}

	public BizException(String code, String desc, Throwable cause) {
		super(desc, cause);
		this.code = code;
		this.desc = desc;
	}

	public BizException(final BizCodeEnum bizCodeEnum) {
		super(bizCodeEnum.getDesc());
		this.code = bizCodeEnum.getCode();
		this.desc = bizCodeEnum.getDesc();
	}

	public BizException(final BizCodeEnum bizCodeEnum, Throwable cause) {
		super(bizCodeEnum.getDesc(), cause);
		this.code = bizCodeEnum.getCode();
		this.desc = bizCodeEnum.getDesc();
	}
}
