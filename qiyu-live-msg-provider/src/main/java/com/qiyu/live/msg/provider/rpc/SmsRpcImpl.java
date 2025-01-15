package com.qiyu.live.msg.provider.rpc;

import com.ddd.qiyu.live.msg.dto.MsgCheckDTO;
import com.ddd.qiyu.live.msg.enums.MsgSendResultEnum;
import com.ddd.qiyu.live.msg.interfaces.ISmsRpc;
import com.qiyu.live.msg.provider.service.ISmsService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class SmsRpcImpl implements ISmsRpc {

    @Resource
    private ISmsService smsService;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return smsService.checkLoginCode(phone,code);
    }

    @Override
    public void insertOne(String phone, Integer code) {
        smsService.insertOne(phone,code);
    }
}