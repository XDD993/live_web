package com.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.ddd.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import com.qiyu.live.im.constants.ImConstants;
import com.qiyu.live.im.constants.ImMsgCodeEnum;
import com.qiyu.live.im.core.server.common.ImContextUtils;
import com.qiyu.live.im.core.server.common.ImMsg;
import com.qiyu.live.im.core.server.handler.SimpleHandler;
import com.qiyu.live.im.dto.ImMsgBody;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HeartBeatMsgHandler implements SimpleHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatMsgHandler.class);
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

	@Override
	public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
		System.out.println("[heartbear]:" + imMsg);
		// 心跳包的基本校验
		Long userId = ImContextUtils.getUserId(ctx);
		Integer appId = ImContextUtils.getAppId(ctx);
		if (userId == null || appId == null) {
			LOGGER.error("attr error, imMsg is {}", imMsg);
			// 有可能是错误的消息包导致，直接放弃连接
			ctx.close();
			throw new IllegalArgumentException("attr error");
		}
		// 心跳包record记录
		String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId, appId);
		this.recordOnlineTime(userId, redisKey);
		this.removeExpireRecord(redisKey);
		redisTemplate.expire(redisKey, 5L, TimeUnit.MINUTES);
		//回写给客户端
		ImMsgBody respBody = new ImMsgBody();
		respBody.setUserId(userId);
		respBody.setAppId(appId);
		respBody.setData("true");
		LOGGER.info("[HeartBeatImMsgHandler] heartbeat msg, userId is {}, appId is {}", userId, appId);
		ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(respBody)));
	}

	/**
	 * 清理掉过期不在线的用户留下的心跳记录（两次心跳时间更友好）
	 * 为什么不直接设置TTL让他自动过期？
	 * 因为我们build redisKey的时候，是对userId%10000进行构建的，一个用户心跳记录只是zset中的一个键值对，而不是整个zset对象
	 */
	private void removeExpireRecord(String redisKey) {
		redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - 2 * ImConstants.DEFAULT_HEART_BEAT_GAP * 1000);
	}

	/**
	 * 记录用户最近一次心跳时间到Redis上
	 */
	private void recordOnlineTime(Long userId, String redisKey) {
		redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
	}
}
