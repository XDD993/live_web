package com.qiyu.live.im.core.server.common;

import com.qiyu.live.im.constants.ImConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 处理消息的解码器
 */
public class ImMsgDecoder extends ByteToMessageDecoder {

	private final int BASE_LEN = 2+4+4;

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
		//进行byteBuf的长度检验 和魔数的检验
		if (byteBuf.readableBytes() >= BASE_LEN ){
			if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC){
				channelHandlerContext.close();
				return;
			}
			int code = byteBuf.readInt();
			int len = byteBuf.readInt();
			//byte数组的字节数小于len 说明数据不完整
			if (byteBuf.readableBytes() < len ){
				channelHandlerContext.close();
				return;
			}
			byte[] body = new byte[len];
			byteBuf.readBytes(body);
			ImMsg imMsg = new ImMsg();
			imMsg.setCode(code);
			imMsg.setLen(len);
			imMsg.setBody(body);
			imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
			out.add(imMsg);
		}
	}
}
