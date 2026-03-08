package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AchievementDTO {
    
    private Long id;
    
    private String badgeId;
    
    private String badgeName;
    
    private String icon;
    
    private String description;
    
    private Boolean unlocked;
    
    private LocalDateTime unlockedAt;
    
    private Boolean isNew;
}
