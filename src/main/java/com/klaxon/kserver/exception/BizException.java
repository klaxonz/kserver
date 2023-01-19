package com.klaxon.kserver.exception;

public class BizException extends RuntimeException {

    private String code;
    private String desc;

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

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
