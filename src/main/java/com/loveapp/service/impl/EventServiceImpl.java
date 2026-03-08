package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.EventDTO;
import com.loveapp.entity.Event;
import com.loveapp.entity.User;
import com.loveapp.mapper.EventMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.EventService;
import com.loveapp.service.IntimacyService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日历事件服务实现
 */
@Slf4j
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private IntimacyService intimacyService;
    
    @Override
    public Map<String, Object> getMonthEvents(int year, int month) {
        Long coupleId = getCoupleId();
        
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        List<Event> events = list(new LambdaQueryWrapper<Event>()
                .eq(Event::getCoupleId, coupleId)
                .ge(Event::getEventDate, startDate)
                .le(Event::getEventDate, endDate)
                .orderByAsc(Event::getEventDate));
        
        List<EventDTO> eventList = events.stream().map(this::toDTO).collect(Collectors.toList());
        
        // 构建日期标记
        Map<String, List<String>> dateMarks = new HashMap<>();
        for (Event event : events) {
            String dateStr = event.getEventDate().toString();
            dateMarks.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(event.getType());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("events", eventList);
        result.put("dateMarks", dateMarks);
        
        return result;
    }
    
    @Override
    public List<EventDTO> getDayEvents(String date) {
        Long coupleId = getCoupleId();
        LocalDate localDate = LocalDate.parse(date);
        
        List<Event> events = list(new LambdaQueryWrapper<Event>()
                .eq(Event::getCoupleId, coupleId)
                .eq(Event::getEventDate, localDate)
                .orderByAsc(Event::getEventTime));
        
        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public Long addEvent(EventDTO dto) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Event event = new Event();
        BeanUtil.copyProperties(dto, event);
        event.setCoupleId(coupleId);
        event.setCreatorId(userId);
        event.setStatus(0);
        
        // 设置默认值
        if (event.getRepeatType() == null) {
            event.setRepeatType("none");
        }
        if (event.getIsAllDay() == null) {
            event.setIsAllDay(1);
        }
        if (event.getIsLunar() == null) {
            event.setIsLunar(0);
        }
        
        // 根据类型设置颜色和图标
        setTypeDefaults(event);
        
        save(event);
        return event.getId();
    }
    
    @Override
    public void updateEvent(EventDTO dto) {
        Long coupleId = getCoupleId();
        
        Event event = getById(dto.getId());
        if (event == null || !event.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.EVENT_NOT_FOUND);
        }
        
        BeanUtil.copyProperties(dto, event, "id", "coupleId", "creatorId", "createdAt");
        updateById(event);
    }
    
    @Override
    public void deleteEvent(Long id) {
        Long coupleId = getCoupleId();
        
        Event event = getById(id);
        if (event == null || !event.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.EVENT_NOT_FOUND);
        }
        
        removeById(id);
    }
    
    @Override
    public void completeEvent(Long id) {
        Long coupleId = getCoupleId();
        
        Event event = getById(id);
        if (event == null || !event.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.EVENT_NOT_FOUND);
        }
        
        event.setStatus(1);
        updateById(event);
        
        // 完成事项积分
        intimacyService.addScore(IntimacyService.ACTION_TODO_COMPLETE);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
    
    private EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        BeanUtil.copyProperties(event, dto);
        return dto;
    }
    
    private void setTypeDefaults(Event event) {
        switch (event.getType()) {
            case Event.TYPE_ANNIVERSARY:
                if (event.getColor() == null) event.setColor("#FF6B9D");
                if (event.getIcon() == null) event.setIcon("💖");
                break;
            case Event.TYPE_TODO:
                if (event.getColor() == null) event.setColor("#4A90D9");
                if (event.getIcon() == null) event.setIcon("✅");
                break;
            case Event.TYPE_DIARY:
                if (event.getColor() == null) event.setColor("#9B59B6");
                if (event.getIcon() == null) event.setIcon("📝");
                break;
            case Event.TYPE_RECIPE:
                if (event.getColor() == null) event.setColor("#F39C12");
                if (event.getIcon() == null) event.setIcon("🍳");
                break;
            case Event.TYPE_PERIOD:
                if (event.getColor() == null) event.setColor("#FFCDD2");
                if (event.getIcon() == null) event.setIcon("🌸");
                break;
        }
    }
}
