package com.ddd.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ddd.provider.dao.po.UserPO;
import org.apache.calcite.adapter.java.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {



}
