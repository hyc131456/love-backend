package com.loveapp.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    
    // 成功
    SUCCESS(0, "success"),
    
    // 通用错误
    ERROR(1000, "操作失败"),
    PARAM_ERROR(1001, "参数错误"),
    NOT_LOGIN(1002, "未登录"),
    NO_PERMISSION(1003, "无权限"),
    NOT_FOUND(1004, "数据不存在"),
    FREQUENT_OPERATION(1005, "操作过于频繁"),
    
    // 用户相关 2xxx
    USER_NOT_EXIST(2001, "用户不存在"),
    WX_LOGIN_ERROR(2002, "微信登录失败"),
    TOKEN_INVALID(2003, "token无效"),
    TOKEN_EXPIRED(2004, "token已过期"),
    
    // 配对相关 3xxx
    INVITE_CODE_INVALID(3001, "邀请码无效"),
    INVITE_CODE_EXPIRED(3002, "邀请码已过期"),
    ALREADY_COUPLED(3003, "已有配对"),
    NOT_COUPLED(3004, "未配对"),
    CANNOT_COUPLE_SELF(3005, "不能和自己配对"),
    
    // 日历相关 4xxx
    EVENT_NOT_FOUND(4001, "事件不存在"),
    
    // 日记相关 5xxx
    DIARY_NOT_FOUND(5001, "日记不存在"),
    DIARY_LIMIT_EXCEEDED(5002, "今日日记已达上限"),
    
    // 文件相关 6xxx
    FILE_UPLOAD_ERROR(6001, "文件上传失败"),
    FILE_TOO_LARGE(6002, "文件过大"),
    FILE_TYPE_NOT_ALLOWED(6003, "文件类型不允许");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
