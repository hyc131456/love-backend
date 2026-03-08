package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("recipes")
public class Recipe {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long coupleId;
    
    private Integer isOfficial;
    
    private String name;
    
    private String category;
    
    private Integer difficulty;
    
    private Integer cookTime;
    
    private String coverImage;
    
    private String ingredients;
    
    private String steps;
    
    private String tips;
    
    private Integer tryCount;
    
    private LocalDate lastTryDate;
    
    private Integer isFavorite;
    
    private Long creatorId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
