package com.qiyu.live.im.core.server.handler;

import com.qiyu.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * 简单工厂模式
 */
public interface ImHandlerFactory {

	/**
	 * 按照ImMsg的code类型去处理对应的消息
	 */
	void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg);

}
