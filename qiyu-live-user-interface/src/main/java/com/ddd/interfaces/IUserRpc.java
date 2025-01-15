package com.ddd.interfaces;

import com.ddd.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserRpc {


	UserDTO getUserById(Long userId);

	Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);
}
