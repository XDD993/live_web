package com.qiyu.live.im.provider.rpc;

import com.qiyu.live.im.interfaces.ImTokenRpc;
import com.qiyu.live.im.provider.service.ImTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {

	@Resource
	private ImTokenService imTokenService;

	@Override
	public String createImLoginToken(Long userId, int appId) {
		return imTokenService.createImLoginToken(userId,appId);

	}

	@Override
	public Long getUserIdByToken(String token) {
		return imTokenService.getUserIdByToken(token);
	}
}
