package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 事件DTO
 */
@Data
public class EventDTO {
    
    private Long id;
    
    /** 事件类型 */
    private String type;
    
    /** 事件标题 */
    private String title;
    
    /** 事件日期 */
    private LocalDate eventDate;
    
    /** 结束日期 */
    private LocalDate endDate;
    
    /** 是否全天 */
    private Integer isAllDay;
    
    /** 时间 */
    private String eventTime;
    
    /** 重复类型 */
    private String repeatType;
    
    /** 重复结束日期 */
    private LocalDate repeatEndDate;
    
    /** 提醒设置 */
    private String reminders;
    
    /** 颜色 */
    private String color;
    
    /** 图标 */
    private String icon;
    
    /** 备注 */
    private String note;
    
    /** 是否农历 */
    private Integer isLunar;
    
    /** 状态 */
    private Integer status;

    /** 下一次发生日期 */
    private LocalDate nextOccurrence;

    /** 距离下一次发生还有多少天 */
    private Long daysUntil;

    /** 提醒摘要 */
    private String reminderSummary;

    /** 是否为系统生成的纪念日 */
    private Integer systemGenerated;
}
