package com.loveapp.service;

/**
 * 亲密值积分服务
 */
public interface IntimacyService {
    
    /**
     * 增加亲密值积分
     * @param actionType 行为类型
     */
    void addScore(String actionType);
    
    // 行为类型常量
    String ACTION_DIARY = "DIARY";           // 发布日记 +2
    String ACTION_TODO_COMPLETE = "TODO";    // 完成事项 +5
    String ACTION_LIKE_COMMENT = "INTERACT"; // 互赞/评论 +1
    String ACTION_ANNIVERSARY = "ANNIV";     // 纪念日互动 +10
    String ACTION_SAVING = "SAVING";         // 记录存款 +3
    String ACTION_RECIPE_TRY = "RECIPE";     // 菜谱尝试 +3
}
