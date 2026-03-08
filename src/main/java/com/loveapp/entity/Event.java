package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日历事件实体
 */
@Data
@TableName("events")
public class Event {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** 事件类型: anniversary/todo/diary/recipe/period */
    private String type;
    
    /** 事件标题 */
    private String title;
    
    /** 事件日期 */
    private LocalDate eventDate;
    
    /** 结束日期 */
    private LocalDate endDate;
    
    /** 是否全天事件 */
    private Integer isAllDay;
    
    /** 时间 HH:mm */
    private String eventTime;
    
    /** 重复类型: none/daily/weekly/monthly/yearly */
    private String repeatType;
    
    /** 重复结束日期 */
    private LocalDate repeatEndDate;
    
    /** 提醒设置JSON */
    private String reminders;
    
    /** 颜色代码 */
    private String color;
    
    /** 图标 */
    private String icon;
    
    /** 备注 */
    private String note;
    
    /** 是否农历 */
    private Integer isLunar;
    
    /** 关联的日记/菜谱ID */
    private Long relatedId;
    
    /** 创建者用户ID */
    private Long creatorId;
    
    /** 状态: 0正常 1已完成 2已删除 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    @TableLogic
    private Integer deleted;
    
    // 类型常量
    public static final String TYPE_ANNIVERSARY = "anniversary";
    public static final String TYPE_TODO = "todo";
    public static final String TYPE_DIARY = "diary";
    public static final String TYPE_RECIPE = "recipe";
    public static final String TYPE_PERIOD = "period";
}
