package com.loveapp.utils;

/**
 * 用户上下文（ThreadLocal）
 */
public class UserContext {
    
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> COUPLE_ID = new ThreadLocal<>();
    
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }
    
    public static Long getUserId() {
        return USER_ID.get();
    }
    
    public static void setCoupleId(Long coupleId) {
        COUPLE_ID.set(coupleId);
    }
    
    public static Long getCoupleId() {
        return COUPLE_ID.get();
    }
    
    public static void clear() {
        USER_ID.remove();
        COUPLE_ID.remove();
    }
}
