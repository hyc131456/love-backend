package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.LoginDTO;
import com.loveapp.dto.UserDTO;
import com.loveapp.entity.Couple;
import com.loveapp.entity.User;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.CoupleService;
import com.loveapp.service.UserService;
import com.loveapp.utils.JwtUtils;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Value("${wechat.appid}")
    private String appid;
    
    @Value("${wechat.secret}")
    private String secret;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private CoupleService coupleService;
    
    @Override
    public LoginDTO wxLogin(String code) {
        // 调用微信接口获取openid
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            appid, secret, code
        );
        
        String result = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(result);
        
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            log.error("微信登录失败: {}", result);
            throw new BusinessException(ResultCode.WX_LOGIN_ERROR);
        }
        
        String openid = json.getStr("openid");
        return doLogin(openid, false);
    }
    
    @Override
    public LoginDTO mockLogin(String openid) {
        return doLogin(openid, true);
    }
    
    private LoginDTO doLogin(String openid, boolean isMock) {
        User user = getByOpenid(openid);
        boolean isNewUser = (user == null);
        
        if (isNewUser) {
            // 创建新用户
            user = new User();
            user.setOpenid(openid);
            user.setNickname(isMock ? "测试用户" : "新用户");
            user.setAvatar("");
            user.setGender(0);
            user.setEnablePeriod(0);
            user.setSharePeriod(0);
            user.setEnableNotification(1);
            save(user);
        }
        
        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId());
        
        // 构建响应
        LoginDTO dto = new LoginDTO();
        dto.setUserId(user.getId());
        dto.setIsNewUser(isNewUser);
        dto.setHasCoupled(user.getCoupleId() != null);
        dto.setCoupleId(user.getCoupleId());
        dto.setToken(token);
        
        UserDTO profile = new UserDTO();
        BeanUtil.copyProperties(user, profile);
        dto.setProfile(profile);
        
        return dto;
    }
    
    @Override
    public UserDTO getCurrentUser() {
        Long userId = UserContext.getUserId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(user, dto);
        return dto;
    }
    
    @Override
    public void updateProfile(UserDTO dto) {
        Long userId = UserContext.getUserId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }
        if (dto.getEnableNotification() != null) {
            user.setEnableNotification(dto.getEnableNotification());
        }
        
        updateById(user);
    }
    
    @Override
    public User getByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
    }
    
    @Override
    public LoginDTO register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User existUser = getByUsername(username);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        // 密码加密（使用简单MD5，生产环境建议使用BCrypt）
        user.setPassword(cn.hutool.crypto.SecureUtil.md5(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setAvatar("");
        user.setGender(0);
        user.setEnablePeriod(0);
        user.setSharePeriod(0);
        user.setEnableNotification(1);
        user.setLastLoginTime(LocalDateTime.now());
        save(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId());
        
        // 构建响应
        LoginDTO dto = new LoginDTO();
        dto.setUserId(user.getId());
        dto.setIsNewUser(true);
        dto.setHasCoupled(false);
        dto.setCoupleId(null);
        dto.setToken(token);
        
        UserDTO profile = new UserDTO();
        BeanUtil.copyProperties(user, profile);
        dto.setProfile(profile);
        
        return dto;
    }
    
    @Override
    public LoginDTO login(String username, String password) {
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        String encryptedPwd = cn.hutool.crypto.SecureUtil.md5(password);
        if (!encryptedPwd.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId());
        
        // 构建响应
        LoginDTO dto = new LoginDTO();
        dto.setUserId(user.getId());
        dto.setIsNewUser(false);
        dto.setHasCoupled(user.getCoupleId() != null);
        dto.setCoupleId(user.getCoupleId());
        dto.setToken(token);
        
        UserDTO profile = new UserDTO();
        BeanUtil.copyProperties(user, profile);
        dto.setProfile(profile);
        
        return dto;
    }
    
    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
