package com.ddd.provider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddd.constants.UserTagFieldNameConstants;
import com.ddd.constants.UserTagsEnum;
import com.ddd.provider.dao.mapper.IUserTagMapper;
import com.ddd.provider.dao.po.UserTagPo;
import com.ddd.provider.service.IUserTagService;
import com.ddd.utils.TagInfoUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {


    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return baseMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo == null) {
            return false;
        }
        String fieldName = userTagsEnum.getFieldName();
        if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_01)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo01(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_02)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo02(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_03)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;
    }
}