package com.qiyu.live.im.core.server;


import com.qiyu.live.im.core.server.common.ImMsgDecoder;
import com.qiyu.live.im.core.server.common.ImMsgEncoder;
import com.qiyu.live.im.core.server.handler.ImServerCoreHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty启动类
 */
public class NettyImServerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerApplication.class);

	//监听的端口
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	//基于netty去启动一个java进程 绑定监听的端口
	public void startApplication(int port) throws InterruptedException {
		setPort(port);
		//处理accept事件
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		//处理read&write事件
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup,workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		//netty初始化相关的handler
		bootstrap.childHandler(new ChannelInitializer<>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				LOGGER.info("初始化连接渠道");
				//设置消息体
				//设计编码器和解码器
				channel.pipeline().addLast(new ImMsgEncoder());
				channel.pipeline().addLast(new ImMsgDecoder());
				channel.pipeline().addLast(new ImServerCoreHandler());

			}
		});
		ChannelFuture channelFuture = bootstrap.bind(port).sync();
		LOGGER.info("Netty服务启动成功,监听端口为{}",getPort());
		//这里会阻塞主线程，实现服务长期开启的效果
		channelFuture.channel().closeFuture().sync();
	}
	public static void main(String[] args) throws InterruptedException {
		NettyImServerApplication nettyImServerApplication = new NettyImServerApplication();
		nettyImServerApplication.startApplication(9090);
	}
}
