package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 用户信息DTO
 */
@Data
public class UserDTO {
    
    /** 用户ID */
    private Long id;
    
    /** 昵称 */
    private String nickname;
    
    /** 头像 */
    private String avatar;
    
    /** 性别 */
    private Integer gender;
    
    /** 生日 */
    private LocalDate birthday;
    
    /** 角色 */
    private String role;
    
    /** 情侣空间ID */
    private Long coupleId;

    /** 是否开启通知 */
    private Integer enableNotification;
}
