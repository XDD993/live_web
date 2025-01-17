package com.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 处理消息的编码器
 */
public class ImMsgEncoder extends MessageToByteEncoder {

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
		ImMsg imMsg = (ImMsg) msg;
		byteBuf.writeShort(imMsg.getMagic());
		//写入code标识
		byteBuf.writeInt(imMsg.getCode());
		//写入消息体的长度
		byteBuf.writeInt(imMsg.getLen());
		//写入消息体
		byteBuf.writeBytes(imMsg.getBody());

//		channelHandlerContext.writeAndFlush(byteBuf);
	}
}
