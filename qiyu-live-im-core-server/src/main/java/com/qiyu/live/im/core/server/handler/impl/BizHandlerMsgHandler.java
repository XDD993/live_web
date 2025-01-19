package com.qiyu.live.im.core.server.handler.impl;

import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class BizHandlerMsgHandler implements SimpleHandler {

	@Override
	public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
		System.out.println("[bizImMsg]:" + imMsg);
		ctx.writeAndFlush(imMsg);
	}
}
