package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情侣空间DTO
 */
@Data
public class CoupleDTO {
    
    /** 空间ID */
    private Long id;
    
    /** 邀请码 */
    private String inviteCode;
    
    /** 邀请码过期时间 */
    private LocalDateTime inviteCodeExpire;
    
    /** 状态 */
    private Integer status;
    
    /** 纪念日 */
    private LocalDate anniversary;
    
    /** 在一起天数 */
    private Long daysTogether;
    
    /** 亲密等级 */
    private String intimacyLevel;
    
    /** 亲密值总分 */
    private Integer intimacyScore;
    
    /** 亲密值进度（百分比） */
    private Integer intimacyProgress;
    
    /** 伴侣信息 */
    private UserDTO partner;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}
