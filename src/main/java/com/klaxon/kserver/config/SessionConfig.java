package com.klaxon.kserver.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    /**
     * 设定cookie序列化器的属性
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        // 用正则表达式配置匹配的域名，可以兼容 localhost、127.0.0.1 等各种场景
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        // cookie生效路径
        serializer.setCookiePath("/");
        // 设置是否只能服务器修改，浏览器端不能修改
        serializer.setUseHttpOnlyCookie(false);
        // 最大生命周期的单位是分钟
        serializer.setCookieMaxAge(24 * 60 * 60);
        return serializer;
    }

}
