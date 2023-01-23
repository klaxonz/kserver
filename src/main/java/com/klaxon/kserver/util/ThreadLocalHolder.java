package com.klaxon.kserver.util;

import com.klaxon.kserver.entity.domain.OnlineUser;

public class ThreadLocalHolder {


    private final static ThreadLocal<OnlineUser> ThreadLocalHolder = new ThreadLocal<>();

    /**
     * 重写ThreadLocal的三个方法：set、get、remove
     */
    public static void set(OnlineUser user) {
        ThreadLocalHolder.set(user);
    }

    public static OnlineUser get() {
        return ThreadLocalHolder.get();
    }

    public static void remove() {
        ThreadLocalHolder.remove();
    }

}