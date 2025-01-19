package com.qiyu.live.im.provider.service.impl;

import com.ddd.qiyu.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import com.qiyu.live.im.provider.service.ImTokenService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImTokenServiceImpl implements ImTokenService {

	@Resource
	private RedisTemplate<String,Object> redisTemplate;
	@Resource
	private ImProviderCacheKeyBuilder imProviderCacheKeyBuilder;

	@Override
	public String createImLoginToken(Long userId, int appId) {
		String token = UUID.randomUUID() + "%" +appId;
		redisTemplate.opsForValue().set(imProviderCacheKeyBuilder.buildImLoginCodeKey(token),userId);
		return token;
	}

	@Override
	public Long getUserIdByToken(String token) {
		Object userId = redisTemplate.opsForValue().get(imProviderCacheKeyBuilder.buildImLoginCodeKey(token));
		return userId == null?null:Long.valueOf((Integer)userId);
	}
}
