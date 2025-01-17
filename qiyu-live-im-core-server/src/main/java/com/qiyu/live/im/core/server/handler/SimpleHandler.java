package com.qiyu.live.im.core.server.handler;

import com.qiyu.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理消息的处理器接口(这里用到的是策略模式)
 */
public interface SimpleHandler {
	void handler(ChannelHandlerContext ctx, ImMsg imMsg);
}
