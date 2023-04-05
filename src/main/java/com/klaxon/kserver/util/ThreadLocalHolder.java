package com.klaxon.kserver.util;

import com.klaxon.kserver.bean.OnlineUser;
import com.klaxon.kserver.mapper.model.WebPage;

public class ThreadLocalHolder {

	private static final ThreadLocal<OnlineUser> userThreadLocalHolder = new ThreadLocal<>();
	private static final ThreadLocal<WebPage> webPageThreadLocalHolder = new ThreadLocal<>();

	private ThreadLocalHolder() {
	}

	public static OnlineUser getUser() {
		return userThreadLocalHolder.get();
	}

	/**
	 * 重写ThreadLocal的三个方法：set、get、remove
	 */
	public static void setUser(OnlineUser user) {
		userThreadLocalHolder.set(user);
	}

	public static void removeUser() {
		userThreadLocalHolder.remove();
	}

	public static WebPage getWebPage() {
		return webPageThreadLocalHolder.get();
	}

	public static void setWebPage(WebPage webPage) {
		webPageThreadLocalHolder.set(webPage);
	}

	public static void removeWebPage() {
		webPageThreadLocalHolder.remove();
	}

}