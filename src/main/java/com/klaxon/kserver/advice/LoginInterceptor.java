package com.klaxon.kserver.advice;

import com.klaxon.kserver.constants.AccountConstant;
import com.klaxon.kserver.entity.domain.OnlineUser;
import com.klaxon.kserver.entity.vo.AccountVo;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AccountVo account = (AccountVo)request.getSession().getAttribute(AccountConstant.SESSION_NAME);
        if (Objects.isNull(account)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
            return false;
        } else {
            OnlineUser onlineUser = new OnlineUser(account.getId(), account.getUsername());
            ThreadLocalHolder.setUser(onlineUser);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalHolder.removeUser();
    }
}
