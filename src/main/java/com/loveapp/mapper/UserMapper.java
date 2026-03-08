package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据OpenID查询用户
     */
    User selectByOpenid(@Param("openid") String openid);
}
