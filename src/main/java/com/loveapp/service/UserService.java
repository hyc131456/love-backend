package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.LoginDTO;
import com.loveapp.dto.UserDTO;
import com.loveapp.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 微信登录
     */
    LoginDTO wxLogin(String code);
    
    /**
     * 模拟登录（开发测试用）
     */
    LoginDTO mockLogin(String openid);
    
    /**
     * 获取当前用户信息
     */
    UserDTO getCurrentUser();
    
    /**
     * 更新用户信息
     */
    void updateProfile(UserDTO dto);
    
    /**
     * 根据OpenID查询用户
     */
    User getByOpenid(String openid);
    
    /**
     * 用户注册
     */
    LoginDTO register(String username, String password, String nickname);
    
    /**
     * 用户名密码登录
     */
    LoginDTO login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
}
