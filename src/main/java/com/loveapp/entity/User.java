package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 密码（加密存储） */
    private String password;
    
    /** 微信OpenID */
    private String openid;
    
    /** 微信UnionID */
    private String unionid;
    
    /** 关联的情侣空间ID */
    private Long coupleId;
    
    /** 在情侣空间中的角色: A/B */
    private String role;
    
    /** 昵称 */
    private String nickname;
    
    /** 头像URL */
    private String avatar;
    
    /** 性别: 0未知 1男 2女 */
    private Integer gender;
    
    /** 生日 */
    private LocalDate birthday;
    
    /** 是否开启姨妈期模块 */
    private Integer enablePeriod;
    
    /** 是否共享姨妈期给伴侣 */
    private Integer sharePeriod;
    
    /** 是否开启通知 */
    private Integer enableNotification;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    @TableLogic
    private Integer deleted;
}
