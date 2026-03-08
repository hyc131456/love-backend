package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("periods")
public class Period {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long coupleId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer cycleLength;
    
    private Integer periodLength;
    
    private String flow;
    
    private String symptoms;
    
    private String note;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
