package com.loveapp.service;

import com.loveapp.dto.AchievementDTO;
import java.util.List;

public interface AchievementService {
    
    List<AchievementDTO> getUnlockedList();
    
    List<AchievementDTO> getAllBadges();
    
    void unlock(String badgeId, String badgeName);
    
    void markAsRead(Long id);
    
    /**
     * 检查并自动解锁成就（由各业务触发点调用）
     */
    void checkAndUnlock();
}
