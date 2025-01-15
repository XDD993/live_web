package com.ddd.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ddd.api.service.IUserLoginService;
import com.ddd.api.vo.UserLoginVO;
import com.ddd.common.vo.WebResponseVO;
import com.ddd.dto.UserLoginDTO;
import com.ddd.interfaces.IUserPhoneRpc;
import com.ddd.qiyu.live.msg.dto.MsgCheckDTO;
import com.ddd.qiyu.live.msg.enums.MsgSendResultEnum;
import com.ddd.qiyu.live.msg.interfaces.ISmsRpc;
import com.qiyu.live.account.interfaces.IAccountTokenRPC;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserLoginServiceImpl implements IUserLoginService {

    private static final String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    @DubboReference
    private ISmsRpc smsRpc;

    @DubboReference
    private IUserPhoneRpc userPhoneRpc;
    
    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        // 参数校验
        if (StringUtils.isEmpty(phone)) {
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if (!Pattern.matches(PHONE_REG, phone)) {
            return WebResponseVO.errorParam("手机号格式错误");
        }
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        // 参数校验
            if (StringUtils.isEmpty(phone)) {
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if (!Pattern.matches(PHONE_REG, phone)) {
            return WebResponseVO.errorParam("手机号格式错误");
        }
        if (code == null || code < 1000) {
            return WebResponseVO.errorParam("验证码格式异常");
        }
        // 检查验证码是否匹配
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if (!msgCheckDTO.isCheckStatus()) {// 校验没通过
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        // 封装token到cookie返回
        UserLoginDTO userLoginDTO = userPhoneRpc.login(phone);
        String token = accountTokenRPC.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie = new Cookie("qytk", token);
        // 设置在哪个域名的访问下，才携带此cookie进行访问
        // https://app.qiyu.live.com//
        // https://api.qiyu.live.com//
        // 取公共部分的顶级域名，如果在hosts中自定义域名有跨域限制无法解决的话就注释掉setDomain和setPath
        // cookie.setDomain("qiyu.live.com");
        // 这里我们不设置域名，就设置为localhost
        cookie.setDomain("localhost");
        // 域名下的所有路径
        cookie.setPath("/");
        // 设置cookie过期时间，单位为秒，设置为token的过期时间，30天
        cookie.setMaxAge(30 * 24 * 3600);
        // 加上它，不然浏览器不会记录cookie,因为在拦截器配置了 这里的就冗余了
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addCookie(cookie);
        return WebResponseVO.success(BeanUtil.copyProperties(userLoginDTO, UserLoginVO.class));
    }
}