package com.ddd.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ddd.provider.dao.po.UserPhonePO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface IUserPhoneMapper extends BaseMapper<UserPhonePO> {
}