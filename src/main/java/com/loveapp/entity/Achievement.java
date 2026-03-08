package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("achievements")
public class Achievement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long coupleId;
    
    private String badgeId;
    
    private String badgeName;
    
    private LocalDateTime unlockedAt;
    
    private Integer isNew;
}
