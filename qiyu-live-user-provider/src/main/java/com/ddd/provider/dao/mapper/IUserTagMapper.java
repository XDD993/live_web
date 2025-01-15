package com.ddd.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ddd.provider.dao.po.UserTagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IUserTagMapper extends BaseMapper<UserTagPo> {

	@Update("update t_user_tag set ${fieldName} = ${fieldName} | #{tag} where user_id = #{userId}")
	int setTag(Long userId, String fieldName, long tag);

	@Update("update t_user_tag set ${fieldName} = ${fieldName} &~ #{tag} where user_id = #{userId}")
	int cancelTag(Long userId, String fieldName, long tag);
}
