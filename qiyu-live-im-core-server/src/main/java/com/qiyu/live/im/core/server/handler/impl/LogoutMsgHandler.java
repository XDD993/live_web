package com.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.qiyu.live.im.constants.ImMsgCodeEnum;
import com.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import com.qiyu.live.im.core.server.common.ImContextUtils;
import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import com.qiyu.live.im.dto.ImMsgBody;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgHandler implements SimpleHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutMsgHandler.class);

	@Override
	public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
		Long userId = ImContextUtils.getUserId(ctx);
		Integer appId = ImContextUtils.getAppId(ctx);
		if(userId == null || appId == null){
			LOGGER.error("attr error, imMsg is {}", imMsg);
			//有可能是错误的消息包导致，直接放弃连接
			ctx.close();
			throw new IllegalArgumentException("attr error");
		}
		//将IM消息回写给客户端
		ImMsgBody respBody = new ImMsgBody();
		respBody.setUserId(userId);
		respBody.setAppId(appId);
		respBody.setData("true");
		ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody)));
		LOGGER.info("[LogoutMsgHandler] logout success, userId is {}, appId is {}", userId, appId);
		//理想情况下：客户端短线的时候发送短线消息包
		ChannelHandlerContextCache.remove(userId);
		ImContextUtils.removeUserId(ctx);
		ImContextUtils.removeAppId(ctx);
		ctx.close();
	}
}
