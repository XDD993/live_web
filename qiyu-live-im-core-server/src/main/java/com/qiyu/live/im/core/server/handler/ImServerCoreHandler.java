package com.qiyu.live.im.core.server.handler;

import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.impl.ImHandlerFactoryImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    
    private ImHandlerFactory imHandlerFactory = new ImHandlerFactoryImpl();
    
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if(!(msg instanceof ImMsg)) {
            throw new IllegalArgumentException("error msg, msg is :" + msg);
        }
        ImMsg imMsg = (ImMsg) msg;
        imHandlerFactory.doMsgHandler(channelHandlerContext, imMsg);
    }
}