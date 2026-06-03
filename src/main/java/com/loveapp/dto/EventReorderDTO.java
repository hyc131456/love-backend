package com.loveapp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 日历事件排序请求
 */
@Data
public class EventReorderDTO {

    /** 事件日期 */
    private LocalDate date;

    /** 排序后的事件ID列表 */
    private List<Long> eventIds;
}
