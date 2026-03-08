package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情侣空间实体
 */
@Data
@TableName("couples")
public class Couple {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 邀请码 */
    private String inviteCode;
    
    /** 邀请码过期时间 */
    private LocalDateTime inviteCodeExpire;
    
    /** 创建者用户ID */
    private Long userA;
    
    /** 加入者用户ID */
    private Long userB;
    
    /** 状态: 0待配对 1已配对 2已解绑 */
    private Integer status;
    
    /** 恋爱纪念日 */
    private LocalDate anniversary;
    
    /** 亲密值总分 */
    private Integer intimacyScore;
    
    /** 亲密等级 */
    private String intimacyLevel;
    
    /** 今日已获积分 */
    private Integer dailyScore;
    
    /** 上次积分日期 */
    private LocalDate lastScoreDate;
    
    /** 日记总数 */
    private Integer diaryCount;
    
    /** 事件总数 */
    private Integer eventCount;
    
    /** 已完成心愿数 */
    private Integer wishCompletedCount;
    
    /** 解绑时间 */
    private LocalDateTime unbindTime;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    @TableLogic
    private Integer deleted;
    
    // 状态常量
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_COUPLED = 1;
    public static final int STATUS_UNBOUND = 2;
}
