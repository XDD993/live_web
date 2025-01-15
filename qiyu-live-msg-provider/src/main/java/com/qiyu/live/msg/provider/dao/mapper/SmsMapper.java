package com.qiyu.live.msg.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiyu.live.msg.provider.dao.po.SmsPO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface SmsMapper extends BaseMapper<SmsPO> {
}