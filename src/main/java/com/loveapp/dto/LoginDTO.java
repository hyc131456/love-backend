package com.loveapp.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginDTO {
    
    /** 用户ID */
    private Long userId;
    
    /** 是否新用户 */
    private Boolean isNewUser;
    
    /** 是否已配对 */
    private Boolean hasCoupled;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** Token */
    private String token;
    
    /** 用户信息 */
    private UserDTO profile;
}
