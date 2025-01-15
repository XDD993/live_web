package com.qiyu.live.web.starter.context;

import com.ddd.common.enums.GatewayHeaderEnum;
import com.qiyu.live.web.starter.constants.RequestConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 旗鱼直播 用户信息拦截器
 */
public class QiyuUserInfoInterceptor implements HandlerInterceptor {

    //所有web请求来到这里的时候，都要被拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        //参数判断，userID是否为空
        //可能走的是白名单url
        if (StringUtils.isEmpty(userIdStr)) {
            System.out.println("白名单请求：放行");
            return true;
        }
        //如果userId不为空，则把它放在线程本地变量里面去
        QiyuRequestContext.set(RequestConstants.QIYU_USER_ID, Long.valueOf(userIdStr));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        QiyuRequestContext.clear();
    }
}