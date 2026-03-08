package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 心愿实体
 */
@Data
@TableName("wishes")
public class Wish {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** 心愿标题 */
    private String title;
    
    /** 详细描述 */
    private String description;
    
    /** 类型: travel/gift/experience/home/study/health/other */
    private String category;
    
    /** 封面图URL */
    private String coverImage;
    
    /** 预算金额 */
    private BigDecimal budget;
    
    /** 期望完成日期 */
    private LocalDate targetDate;
    
    /** 状态: 0未开始 1进行中 2已完成 3已放弃 */
    private Integer status;
    
    /** 关联的储蓄目标ID */
    private Long linkedSavingId;
    
    /** 创建者用户ID */
    private Long creatorId;
    
    /** 完成时间 */
    private LocalDateTime completedTime;
    
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
    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_ABANDONED = 3;
}
