package com.ddd.provider.rpc;

import com.ddd.dto.UserDTO;
import com.ddd.interfaces.IUserRpc;
import com.ddd.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

@DubboService
public class UserRpcImpl implements IUserRpc {

	@Resource
	private IUserService userService;


	@Override
	public UserDTO getUserById(Long userId) {
		return userService.getUserById(userId);
	}

	@Override
	public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {

		return userService.batchQueryUserInfo(userIdList);
	}
}
