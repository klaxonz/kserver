package com.klaxon.kserver.pojo;


import com.klaxon.kserver.exception.BizCodeEnum;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;

public class Response<T> {

    private String code;
    private String desc;
    private Date timestamp = new Date();
    private T data;

    private Response() {
    }

    private Response(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Response(final BizCodeEnum bizCodeEnum) {
        this.code = bizCodeEnum.getCode();
        this.desc = bizCodeEnum.getDesc();
    }

    private Response(final BizCodeEnum bizCodeEnum, final T data) {
        this(bizCodeEnum);
        this.data = data;
    }

    public static <R> Response<R> success(){
        return new Response<R>(BizCodeEnum.COMMON_SUCCESS);
    }

    public static <R> Response<R> success(final String desc){
        return new Response<R>(BizCodeEnum.COMMON_SUCCESS.getCode(), desc);
    }

    public static <R> Response<R> success(R data) {
        return new Response<R>(BizCodeEnum.COMMON_SUCCESS, data);
    }

    public static <R> Response<R> error() {
        return new Response<R>(BizCodeEnum.COMMON_ERROR);
    }

    public static <R> Response<R> error(final String code) {
        return new Response<R>(code, Strings.EMPTY);
    }

    public static <R> Response<R> error(final String code, final String desc) {
        return new Response<R>(code, desc);
    }

    public static <R> Response<R> error(final BizCodeEnum bizCodeEnum) {
        return new Response<R>(bizCodeEnum);
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}