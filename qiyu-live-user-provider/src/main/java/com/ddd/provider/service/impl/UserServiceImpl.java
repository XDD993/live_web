package com.ddd.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.ddd.common.utils.ConvertBeanUtils;
import com.ddd.dto.UserDTO;
import com.ddd.provider.dao.mapper.UserMapper;
import com.ddd.provider.dao.po.UserPO;
import com.ddd.provider.service.IUserService;
import com.ddd.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

	@Resource
	private UserMapper userMapper;
	@Resource
	private RedisTemplate<String,UserDTO> redisTemplate;
	@Resource
	private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
	@Resource
	private MQProducer mqProducer;

	@Override
	public UserDTO getUserById(Long userId) {
		if (userId == null){
			return null;
		}
		String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
		UserPO userPO = userMapper.selectById(userId);
		System.out.println(userPO+"====================");
		try {
			Message message = new Message();
			message.setBody(JSON.toJSONString(userPO).getBytes());
			message.setTopic("user-update-cache");
			message.setDelayTimeLevel(1);
			mqProducer.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		UserDTO userDTO = ConvertBeanUtils.convert(userPO, UserDTO.class);
		if (userDTO!=null){
			redisTemplate.opsForValue().set(key,userDTO);
		}
		return userDTO;
	}


	@Override
	public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
		if(CollectionUtils.isEmpty(userIdList)){
			return Maps.newHashMap();
		}
		userIdList =  userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(userIdList)){
			return Maps.newHashMap();
		}
		/**
		 * 先使用传过来的id集合去redis里面查询 这些从redis里面有数据的id就不再去请求数据库
		 * 假如redis里有个别id的数据没有 就继续查询数据库并缓存到redis里
		 */
		//先使用key生成工具 把id转成需要的key,并查询redis
		List<String> keyList = new ArrayList<>();
		userIdList.forEach(id->{
			keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(id));
		});
		List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(keyList).stream().filter(x -> x != null).collect(Collectors.toList());
		if (userDTOList.size()==userIdList.size()){
			return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
		}
		//这些是已经有缓存数据的id集合，和需要使用数据库查询的id集合
		List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
		List<Long> userInNotCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x)).collect(Collectors.toList());

		//使用多线程查询代替使用原生的数据库连接查询
		Map<Long, List<Long>> userIdmMap = userInNotCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
		List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
		userIdmMap.values().parallelStream().forEach(queryUserIdList->{
			dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserIdList),UserDTO.class));
		});
		if (!CollectionUtils.isEmpty(dbQueryResult)){
			Map<String, UserDTO> saveMap = dbQueryResult.stream().collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), x -> x));
			redisTemplate.opsForValue().multiSet(saveMap);
			//使用管道设置减少IO开销
			redisTemplate.executePipelined(new SessionCallback<Object>() {
				@Override
				public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
					for (String key : saveMap.keySet()) {
						operations.expire((K) key ,createRandomExpireTime(), TimeUnit.SECONDS);
					}
					return null;
				}
			});
			userDTOList.addAll(dbQueryResult);
		}
		return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
	}

	@Override
	public boolean insertOne(UserDTO userDTO) {
		if(userDTO == null || userDTO.getUserId() == null) {
			return false;
		}
		userMapper.insert(BeanUtil.copyProperties(userDTO, UserPO.class));
		return true;
	}

	private long createRandomExpireTime() {
		return ThreadLocalRandom.current().nextLong(1000) + 60 * 30;//30min + 1000s
	}
}
