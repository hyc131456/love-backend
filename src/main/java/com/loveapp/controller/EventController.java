package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.EventDTO;
import com.loveapp.service.EventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 日历事件控制器
 */
@Slf4j
@RestController
@RequestMapping("/calendar")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    /**
     * 获取月事件
     */
    @GetMapping("/events")
    public Result<Map<String, Object>> getMonthEvents(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> data = eventService.getMonthEvents(year, month);
        return Result.success(data);
    }
    
    /**
     * 获取日事件
     */
    @GetMapping("/events/day")
    public Result<List<EventDTO>> getDayEvents(@RequestParam String date) {
        List<EventDTO> list = eventService.getDayEvents(date);
        return Result.success(list);
    }

    /**
     * 获取纪念日列表
     */
    @GetMapping("/events/anniversaries")
    public Result<List<EventDTO>> getAnniversaryEvents() {
        List<EventDTO> list = eventService.getAnniversaryEvents();
        return Result.success(list);
    }

    /**
     * 获取最近的纪念日提醒
     */
    @GetMapping("/events/upcoming-reminder")
    public Result<EventDTO> getUpcomingReminder() {
        EventDTO dto = eventService.getUpcomingReminder();
        return Result.success(dto);
    }
    
    /**
     * 添加事件
     */
    @PostMapping("/event")
    public Result<Long> addEvent(@RequestBody @Valid EventDTO dto) {
        Long id = eventService.addEvent(dto);
        return Result.success(id);
    }
    
    /**
     * 更新事件
     */
    @PutMapping("/event")
    public Result<Void> updateEvent(@RequestBody @Valid EventDTO dto) {
        eventService.updateEvent(dto);
        return Result.success();
    }
    
    /**
     * 删除事件
     */
    @DeleteMapping("/event/{id}")
    public Result<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Result.success();
    }
    
    /**
     * 完成事项
     */
    @PostMapping("/event/{id}/complete")
    public Result<Void> completeEvent(@PathVariable Long id) {
        eventService.completeEvent(id);
        return Result.success();
    }
}
