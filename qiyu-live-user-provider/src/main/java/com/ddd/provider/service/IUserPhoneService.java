package com.ddd.provider.service;



import com.ddd.dto.UserLoginDTO;
import com.ddd.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneService {

    //登录 + 注册初始化
    //userId + token
    UserLoginDTO login(String phone);

    //根据手机号找到相应用户ID
    UserPhoneDTO queryByPhone(String phone);

    //根据用户Id查询手机号(一对多：一个用户有多个手机号)
    List<UserPhoneDTO> queryByUserId(Long userId);
}