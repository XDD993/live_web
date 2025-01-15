package com.ddd.api.controller;

import com.ddd.dto.UserDTO;
import com.ddd.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

	@DubboReference
	private IUserRpc userRpc;


	@GetMapping("/getUserInfo")
	public UserDTO getUserInfo(Long userId){
		return userRpc.getUserById(userId);
	}

	@GetMapping("/batchQueryUserInfo")
	public Map<Long,UserDTO>batchQueryUserInfo(String userIdStr){
		return userRpc.batchQueryUserInfo(Arrays.asList(userIdStr.split(",")).stream().map(x->Long.valueOf(x)).collect(Collectors.toList()));
	}
}
