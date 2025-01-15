package com.ddd.provider.service;

import com.ddd.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserService {

	UserDTO getUserById(Long userId);


	Map<Long, UserDTO> batchQueryUserInfo(List<Long> userId);

	boolean insertOne(UserDTO userDTO);

}

