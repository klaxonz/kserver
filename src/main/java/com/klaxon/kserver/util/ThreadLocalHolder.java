package com.klaxon.kserver.util;

import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.domain.OnlineUser;

public class ThreadLocalHolder {


    private final static ThreadLocal<OnlineUser> userThreadLocalHolder = new ThreadLocal<>();
    private final static ThreadLocal<WebPage> webPageThreadLocalHolder = new ThreadLocal<>();

    /**
     * 重写ThreadLocal的三个方法：set、get、remove
     */
    public static void setUser(OnlineUser user) {
        userThreadLocalHolder.set(user);
    }

    public static OnlineUser getUser() {
        return userThreadLocalHolder.get();
    }

    public static void removeUser() {
        userThreadLocalHolder.remove();
    }

    public static void setWebPage(WebPage webPage) {
        webPageThreadLocalHolder.set(webPage);
    }

    public static WebPage getWebPage() {
        return webPageThreadLocalHolder.get();
    }

    public static void removeWebPage() {
        webPageThreadLocalHolder.remove();
    }

}