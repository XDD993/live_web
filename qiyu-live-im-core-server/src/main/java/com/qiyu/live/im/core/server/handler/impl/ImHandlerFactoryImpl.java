package com.qiyu.live.im.core.server.handler.impl;

import com.qiyu.live.im.constants.ImMsgCodeEnum;
import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.ImHandlerFactory;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单工厂的实现类
 */
public class ImHandlerFactoryImpl implements ImHandlerFactory {

	//里面存放的是不同的策略处理器，实现单例模式，让这个map在启动的时候就进行初始化。
	private static Map<Integer, SimpleHandler > simpleHandlerMap = new HashMap<>();

	//这里使用的就是单例模式
	static {
		//登录消息包：登录token验证，channel 和 userId 关联
		//登出消息包：正常断开im连接时发送的
		//业务消息包：最常用的消息类型，例如我们的im收发数据
		//心跳消息包：定时给im发送心跳包
		simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), new LoginMsgHandler());
		simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), new LogoutMsgHandler());
		simpleHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), new BizHandlerMsgHandler());
		simpleHandlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), new HeartBeatMsgHandler());
	}

	@Override
	public void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
		SimpleHandler simpleHandler = simpleHandlerMap.get(imMsg.getCode());
		if(simpleHandler == null) {
			throw new IllegalArgumentException("msg code is error, code is :" + imMsg.getCode());
		}
		simpleHandler.handler(ctx, imMsg);

	}
}
