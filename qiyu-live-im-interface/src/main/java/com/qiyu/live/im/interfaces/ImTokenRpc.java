package com.qiyu.live.im.interfaces;

public interface ImTokenRpc {
	/**
	 * 创建用户登录im服务的token
	 */
	String createImLoginToken(Long userId,int appId);

	/**
	 * 根据用户Id获取Token
	 */
	Long getUserIdByToken(String token);
}
