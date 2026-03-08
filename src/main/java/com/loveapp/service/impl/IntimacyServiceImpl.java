package com.loveapp.service.impl;

import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.entity.Couple;
import com.loveapp.entity.User;
import com.loveapp.mapper.CoupleMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.IntimacyService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class IntimacyServiceImpl implements IntimacyService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CoupleMapper coupleMapper;
    
    /** 每日积分上限 */
    private static final int DAILY_LIMIT = 20;
    
    /** 各行为积分值 */
    private static final Map<String, Integer> SCORE_MAP = Map.of(
        ACTION_DIARY, 2,
        ACTION_TODO_COMPLETE, 5,
        ACTION_LIKE_COMMENT, 1,
        ACTION_ANNIVERSARY, 10,
        ACTION_SAVING, 3,
        ACTION_RECIPE_TRY, 3
    );
    
    /** 等级定义：积分阈值 -> 等级名 */
    private static final int[][] LEVEL_THRESHOLDS = {
        {0, 1},      // Lv.1 热恋期
        {101, 2},     // Lv.2 稳定期
        {301, 3},     // Lv.3 老夫老妻
        {501, 4}      // Lv.4 灵魂伴侣
    };
    
    private static final Map<Integer, String> LEVEL_NAMES = Map.of(
        1, "热恋期",
        2, "稳定期",
        3, "老夫老妻",
        4, "灵魂伴侣"
    );
    
    @Override
    public void addScore(String actionType) {
        try {
            Long userId = UserContext.getUserId();
            User user = userMapper.selectById(userId);
            if (user == null || user.getCoupleId() == null) {
                return; // 未配对则静默跳过
            }
            
            Couple couple = coupleMapper.selectById(user.getCoupleId());
            if (couple == null || couple.getStatus() != Couple.STATUS_COUPLED) {
                return;
            }
            
            Integer scoreToAdd = SCORE_MAP.get(actionType);
            if (scoreToAdd == null) {
                log.warn("未知的积分行为类型: {}", actionType);
                return;
            }
            
            // 跨天重置每日积分
            LocalDate today = LocalDate.now();
            if (couple.getLastScoreDate() == null || !couple.getLastScoreDate().equals(today)) {
                couple.setDailyScore(0);
                couple.setLastScoreDate(today);
            }
            
            // 检查每日上限
            if (couple.getDailyScore() >= DAILY_LIMIT) {
                log.debug("已达今日积分上限 coupleId={}", couple.getId());
                return;
            }
            
            // 实际可加积分 = min(行为积分, 剩余额度)
            int remaining = DAILY_LIMIT - couple.getDailyScore();
            int actualScore = Math.min(scoreToAdd, remaining);
            
            couple.setDailyScore(couple.getDailyScore() + actualScore);
            couple.setIntimacyScore(
                (couple.getIntimacyScore() == null ? 0 : couple.getIntimacyScore()) + actualScore
            );
            
            // 更新等级
            couple.setIntimacyLevel(calculateLevel(couple.getIntimacyScore()));
            
            coupleMapper.updateById(couple);
            log.info("亲密值增加: coupleId={}, action={}, +{}, total={}", 
                couple.getId(), actionType, actualScore, couple.getIntimacyScore());
                
        } catch (Exception e) {
            // 积分是辅助功能，不应阻断主流程
            log.error("增加亲密值积分失败", e);
        }
    }
    
    private String calculateLevel(int score) {
        int level = 1;
        for (int[] threshold : LEVEL_THRESHOLDS) {
            if (score >= threshold[0]) {
                level = threshold[1];
            }
        }
        return LEVEL_NAMES.getOrDefault(level, "热恋期");
    }
}
