package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 储蓄目标实体
 */
@Data
@TableName("savings")
public class Saving {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** 目标名称 */
    private String name;
    
    /** 图标 */
    private String icon;
    
    /** 目标金额 */
    private BigDecimal targetAmount;
    
    /** 当前金额 */
    private BigDecimal currentAmount;
    
    /** 截止日期 */
    private LocalDate deadline;
    
    /** 关联的心愿ID */
    private Long linkedWishId;
    
    /** A的贡献金额 */
    private BigDecimal userAAmount;
    
    /** B的贡献金额 */
    private BigDecimal userBAmount;
    
    /** 状态: 0进行中 1已完成 2已放弃 */
    private Integer status;
    
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
}
