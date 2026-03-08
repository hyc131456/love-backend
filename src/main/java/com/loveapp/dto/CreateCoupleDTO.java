package com.loveapp.dto;

import lombok.Data;

/**
 * 创建空间响应DTO
 */
@Data
public class CreateCoupleDTO {
    
    /** 空间ID */
    private Long coupleId;
    
    /** 邀请码 */
    private String inviteCode;
    
    /** 过期时间 */
    private String expireTime;
}
