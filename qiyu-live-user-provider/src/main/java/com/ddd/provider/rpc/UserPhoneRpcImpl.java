package com.ddd.provider.rpc;

import com.ddd.dto.UserLoginDTO;
import com.ddd.dto.UserPhoneDTO;
import com.ddd.interfaces.IUserPhoneRpc;
import com.ddd.provider.service.IUserPhoneService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;


import java.util.List;

@DubboService
public class UserPhoneRpcImpl implements IUserPhoneRpc {
    
    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }


    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }
}