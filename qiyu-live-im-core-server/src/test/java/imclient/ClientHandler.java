package imclient;

import com.qiyu.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ImMsg imMsg = (ImMsg) msg;
		System.out.println("【服务端响应数据】 result is " + imMsg);
	}
}
