package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 存款记录实体
 */
@Data
@TableName("saving_records")
public class SavingRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 储蓄目标ID */
    private Long savingId;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** 存款人用户ID */
    private Long userId;
    
    /** 存款金额 */
    private BigDecimal amount;
    
    /** 备注 */
    private String note;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
