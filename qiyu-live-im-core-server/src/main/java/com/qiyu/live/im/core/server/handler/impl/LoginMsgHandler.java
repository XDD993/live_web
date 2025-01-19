package com.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.ddd.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import com.qiyu.live.im.constants.AppIdEnum;
import com.qiyu.live.im.constants.ImMsgCodeEnum;
import com.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import com.qiyu.live.im.core.server.common.ImContextUtils;
import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import com.qiyu.live.im.dto.ImMsgBody;
import com.qiyu.live.im.interfaces.ImTokenRpc;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class LoginMsgHandler implements SimpleHandler {

	private static Logger logger = LoggerFactory.getLogger(LoginMsgHandler.class);

	@Resource
	private ImTokenRpc imTokenRpc;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

	@Override
	public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
		byte[] body = imMsg.getBody();
		if (body==null && body.length==0 ){
			ctx.close();
			logger.error("body error,imMsg is {}",imMsg);
			throw new IllegalArgumentException("body error");
		}
		ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
		String token = imMsgBody.getToken();
		Long userIdFromMsg = imMsgBody.getUserId();
		Integer appId = imMsgBody.getAppId();
		if (StringUtils.isEmpty(token)){
			ctx.close();
			logger.error("token error,imMsg is {}",imMsg);
			throw new IllegalArgumentException("token error");
		}
		Long userId = imTokenRpc.getUserIdByToken(token);
		// 从RPC获取的userId和传递过来的userId相等，则没出现差错，允许建立连接
		if (userId != null && userId.equals(userIdFromMsg)) {
			// 按照userId保存好相关的channel信息
			ChannelHandlerContextCache.put(userId, ctx);
			// 将userId保存到netty域信息中，用于正常/非正常logout的处理
			ImContextUtils.setUserId(ctx, userId);
			ImContextUtils.setAppId(ctx, appId);
			// 将im消息回写给客户端
			ImMsgBody respBody = new ImMsgBody();
			respBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
			respBody.setUserId(userId);
			respBody.setData("true");
			ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
			logger.info("[LoginMsgHandler] login success, userId is {}, appId is {}", userId, appId);
			ctx.writeAndFlush(respMsg);
			stringRedisTemplate.delete(cacheKeyBuilder.buildImLoginTokenKey(userId, appId));
			return;
		}
		ctx.close();
		logger.error("token error, imMsg is {}", imMsg);
		throw new IllegalArgumentException("token error");
	}
}
