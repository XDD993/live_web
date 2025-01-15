package com.ddd.api.service;

import com.ddd.common.vo.WebResponseVO;
import jakarta.servlet.http.HttpServletResponse;


public interface IUserLoginService {

    /**
     * 发送登录验证码
     *
     * @param phone
     * @return
     */
    WebResponseVO sendLoginCode(String phone);

    /**
     * 手机号+验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}