package com.ddd.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddd.constants.UserTagsEnum;
import com.ddd.provider.dao.po.UserTagPo;

public interface IUserTagService extends IService<UserTagPo> {

    /**
     * 设置标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean setTag(Long userId, UserTagsEnum userTagsEnum);

    /**
     * 取消标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean cancelTag(Long userId, UserTagsEnum userTagsEnum);

    /**
     * 是否包含某个标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean containTag(Long userId,UserTagsEnum userTagsEnum);
}