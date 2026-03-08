package com.loveapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.AchievementDTO;
import com.loveapp.entity.*;
import com.loveapp.mapper.*;
import com.loveapp.service.AchievementService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AchievementServiceImpl implements AchievementService {
    
    @Autowired
    private AchievementMapper achievementMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CoupleMapper coupleMapper;
    
    @Autowired
    private DiaryMapper diaryMapper;
    
    @Autowired
    private EventMapper eventMapper;
    
    @Autowired
    private WishMapper wishMapper;
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Autowired
    private SavingMapper savingMapper;
    
    // 徽章定义（权威数据源，前端从此获取）
    private static final List<Map<String, String>> BADGE_DEFINITIONS = Arrays.asList(
        Map.of("id", "first_meet", "name", "初次相遇", "icon", "💕", "desc", "完成配对"),
        Map.of("id", "first_diary", "name", "初心", "icon", "📝", "desc", "发布第一篇日记"),
        Map.of("id", "diary_10", "name", "记录者", "icon", "📚", "desc", "累计发布10篇日记"),
        Map.of("id", "diary_50", "name", "回忆收藏家", "icon", "🎭", "desc", "累计发布50篇日记"),
        Map.of("id", "first_wish", "name", "许愿人", "icon", "⭐", "desc", "创建第一个心愿"),
        Map.of("id", "wish_complete", "name", "圆梦者", "icon", "🌟", "desc", "完成第一个心愿"),
        Map.of("id", "saving_1000", "name", "小金库", "icon", "💰", "desc", "储蓄达到1000元"),
        Map.of("id", "together_30", "name", "一个月", "icon", "🌙", "desc", "在一起30天"),
        Map.of("id", "together_100", "name", "百日纪念", "icon", "💯", "desc", "在一起100天"),
        Map.of("id", "together_365", "name", "周年庆", "icon", "🎂", "desc", "在一起一年"),
        Map.of("id", "first_recipe", "name", "初试锋芒", "icon", "🍳", "desc", "创建第一个菜谱"),
        Map.of("id", "recipe_10", "name", "家庭大厨", "icon", "👨‍🍳", "desc", "累计10个菜谱")
    );
    
    @Override
    public List<AchievementDTO> getUnlockedList() {
        Long coupleId = getCoupleId();
        
        List<Achievement> achievements = achievementMapper.selectList(
                new LambdaQueryWrapper<Achievement>()
                        .eq(Achievement::getCoupleId, coupleId)
                        .orderByDesc(Achievement::getUnlockedAt));
        
        return achievements.stream().map(a -> {
            AchievementDTO dto = new AchievementDTO();
            dto.setId(a.getId());
            dto.setBadgeId(a.getBadgeId());
            dto.setBadgeName(a.getBadgeName());
            dto.setUnlocked(true);
            dto.setUnlockedAt(a.getUnlockedAt());
            dto.setIsNew(a.getIsNew() == 1);
            
            BADGE_DEFINITIONS.stream()
                    .filter(b -> b.get("id").equals(a.getBadgeId()))
                    .findFirst()
                    .ifPresent(b -> {
                        dto.setIcon(b.get("icon"));
                        dto.setDescription(b.get("desc"));
                    });
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<AchievementDTO> getAllBadges() {
        Long coupleId = getCoupleId();
        
        // 查出已解锁的
        Map<String, Achievement> unlockedMap = achievementMapper.selectList(
                new LambdaQueryWrapper<Achievement>()
                        .eq(Achievement::getCoupleId, coupleId))
                .stream()
                .collect(Collectors.toMap(Achievement::getBadgeId, a -> a, (a, b) -> a));
        
        return BADGE_DEFINITIONS.stream().map(b -> {
            AchievementDTO dto = new AchievementDTO();
            dto.setBadgeId(b.get("id"));
            dto.setBadgeName(b.get("name"));
            dto.setIcon(b.get("icon"));
            dto.setDescription(b.get("desc"));
            
            Achievement unlocked = unlockedMap.get(b.get("id"));
            if (unlocked != null) {
                dto.setId(unlocked.getId());
                dto.setUnlocked(true);
                dto.setUnlockedAt(unlocked.getUnlockedAt());
                dto.setIsNew(unlocked.getIsNew() == 1);
            } else {
                dto.setUnlocked(false);
                dto.setIsNew(false);
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void unlock(String badgeId, String badgeName) {
        Long coupleId = getCoupleId();
        doUnlock(coupleId, badgeId, badgeName);
    }
    
    @Override
    public void markAsRead(Long id) {
        Achievement achievement = achievementMapper.selectById(id);
        if (achievement != null) {
            achievement.setIsNew(0);
            achievementMapper.updateById(achievement);
        }
    }
    
    @Override
    public void checkAndUnlock() {
        try {
            Long userId = UserContext.getUserId();
            User user = userMapper.selectById(userId);
            if (user == null || user.getCoupleId() == null) {
                return;
            }
            
            Long coupleId = user.getCoupleId();
            Couple couple = coupleMapper.selectById(coupleId);
            if (couple == null || couple.getStatus() != Couple.STATUS_COUPLED) {
                return;
            }
            
            // 获取已解锁的徽章ID集合
            Set<String> unlockedIds = achievementMapper.selectList(
                    new LambdaQueryWrapper<Achievement>()
                            .eq(Achievement::getCoupleId, coupleId))
                    .stream()
                    .map(Achievement::getBadgeId)
                    .collect(Collectors.toSet());
            
            // === 配对成就 ===
            if (!unlockedIds.contains("first_meet")) {
                doUnlock(coupleId, "first_meet", "初次相遇");
            }
            
            // === 日记成就 ===
            long diaryCount = diaryMapper.selectCount(
                    new LambdaQueryWrapper<Diary>()
                            .eq(Diary::getCoupleId, coupleId)
                            .eq(Diary::getIsDraft, 0));
            
            if (diaryCount >= 1 && !unlockedIds.contains("first_diary")) {
                doUnlock(coupleId, "first_diary", "初心");
            }
            if (diaryCount >= 10 && !unlockedIds.contains("diary_10")) {
                doUnlock(coupleId, "diary_10", "记录者");
            }
            if (diaryCount >= 50 && !unlockedIds.contains("diary_50")) {
                doUnlock(coupleId, "diary_50", "回忆收藏家");
            }
            
            // === 心愿成就 ===
            long wishTotal = wishMapper.selectCount(
                    new LambdaQueryWrapper<Wish>()
                            .eq(Wish::getCoupleId, coupleId));
            long wishCompleted = wishMapper.selectCount(
                    new LambdaQueryWrapper<Wish>()
                            .eq(Wish::getCoupleId, coupleId)
                            .eq(Wish::getStatus, 2));
            
            if (wishTotal >= 1 && !unlockedIds.contains("first_wish")) {
                doUnlock(coupleId, "first_wish", "许愿人");
            }
            if (wishCompleted >= 1 && !unlockedIds.contains("wish_complete")) {
                doUnlock(coupleId, "wish_complete", "圆梦者");
            }
            
            // === 储蓄成就 ===
            long savingOver1000 = savingMapper.selectCount(
                    new LambdaQueryWrapper<Saving>()
                            .eq(Saving::getCoupleId, coupleId)
                            .ge(Saving::getCurrentAmount, 1000));
            
            if (savingOver1000 >= 1 && !unlockedIds.contains("saving_1000")) {
                doUnlock(coupleId, "saving_1000", "小金库");
            }
            
            // === 在一起天数成就 ===
            if (couple.getAnniversary() != null) {
                long daysTogether = ChronoUnit.DAYS.between(
                        couple.getAnniversary(), java.time.LocalDate.now());
                
                if (daysTogether >= 30 && !unlockedIds.contains("together_30")) {
                    doUnlock(coupleId, "together_30", "一个月");
                }
                if (daysTogether >= 100 && !unlockedIds.contains("together_100")) {
                    doUnlock(coupleId, "together_100", "百日纪念");
                }
                if (daysTogether >= 365 && !unlockedIds.contains("together_365")) {
                    doUnlock(coupleId, "together_365", "周年庆");
                }
            }
            
            // === 菜谱成就 ===
            long recipeCount = recipeMapper.selectCount(
                    new LambdaQueryWrapper<Recipe>()
                            .eq(Recipe::getCoupleId, coupleId)
                            .eq(Recipe::getIsOfficial, 0));
            
            if (recipeCount >= 1 && !unlockedIds.contains("first_recipe")) {
                doUnlock(coupleId, "first_recipe", "初试锋芒");
            }
            if (recipeCount >= 10 && !unlockedIds.contains("recipe_10")) {
                doUnlock(coupleId, "recipe_10", "家庭大厨");
            }
            
        } catch (Exception e) {
            // 成就检查不应阻断主流程
            log.error("检查成就解锁失败", e);
        }
    }
    
    // ========== 内部方法 ==========
    
    private void doUnlock(Long coupleId, String badgeId, String badgeName) {
        // 幂等检查
        Long count = achievementMapper.selectCount(
                new LambdaQueryWrapper<Achievement>()
                        .eq(Achievement::getCoupleId, coupleId)
                        .eq(Achievement::getBadgeId, badgeId));
        
        if (count > 0) return;
        
        Achievement achievement = new Achievement();
        achievement.setCoupleId(coupleId);
        achievement.setBadgeId(badgeId);
        achievement.setBadgeName(badgeName);
        achievement.setUnlockedAt(LocalDateTime.now());
        achievement.setIsNew(1);
        achievementMapper.insert(achievement);
        
        log.info("成就解锁: coupleId={}, badge={}", coupleId, badgeId);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
}
