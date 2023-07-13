package com.klaxon.kserver.exception;


public class BizException extends RuntimeException {

	private static final long serialVersionUID = -109589250839197760L;

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

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
