package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.EventDTO;
import com.loveapp.entity.Event;

import java.util.List;
import java.util.Map;

/**
 * 日历事件服务接口
 */
public interface EventService extends IService<Event> {
    
    /**
     * 获取月事件列表
     */
    Map<String, Object> getMonthEvents(int year, int month);
    
    /**
     * 获取日事件列表
     */
    List<EventDTO> getDayEvents(String date);
    
    /**
     * 添加事件
     */
    Long addEvent(EventDTO dto);
    
    /**
     * 更新事件
     */
    void updateEvent(EventDTO dto);
    
    /**
     * 删除事件
     */
    void deleteEvent(Long id);
    
    /**
     * 完成事项
     */
    void completeEvent(Long id);
}
