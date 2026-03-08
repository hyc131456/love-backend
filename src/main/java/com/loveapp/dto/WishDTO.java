package com.loveapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 心愿DTO
 */
@Data
public class WishDTO {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String category;
    
    private String coverImage;
    
    private BigDecimal budget;
    
    private LocalDate targetDate;
    
    private Integer status;
    
    private Long linkedSavingId;
    
    private Long creatorId;
    
    private LocalDateTime completedTime;
    
    private LocalDateTime createdAt;
}
