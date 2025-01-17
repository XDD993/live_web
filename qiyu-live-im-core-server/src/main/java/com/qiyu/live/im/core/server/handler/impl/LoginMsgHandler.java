package com.qiyu.live.im.core.server.handler.impl;

import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import io.netty.channel.ChannelHandlerContext;

public class LoginMsgHandler implements SimpleHandler {

	@Override
	public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
		System.out.println("[login]:" + imMsg);
		ctx.writeAndFlush(imMsg);
	}
}
