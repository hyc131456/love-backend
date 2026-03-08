package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.PeriodDTO;
import com.loveapp.entity.Period;
import com.loveapp.entity.User;
import com.loveapp.mapper.PeriodMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.PeriodService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PeriodServiceImpl extends ServiceImpl<PeriodMapper, Period> implements PeriodService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<PeriodDTO> getList() {
        Long userId = UserContext.getUserId();
        
        LambdaQueryWrapper<Period> wrapper = new LambdaQueryWrapper<Period>()
                .eq(Period::getUserId, userId)
                .orderByDesc(Period::getStartDate);
        
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public Long record(PeriodDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        Period period = new Period();
        BeanUtil.copyProperties(dto, period);
        period.setUserId(userId);
        period.setCoupleId(user.getCoupleId());
        
        if (period.getCycleLength() == null) {
            period.setCycleLength(28);
        }
        if (period.getPeriodLength() == null) {
            period.setPeriodLength(5);
        }
        
        save(period);
        return period.getId();
    }
    
    @Override
    public void update(PeriodDTO dto) {
        Period period = getById(dto.getId());
        if (period == null || !period.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        BeanUtil.copyProperties(dto, period, "id", "userId", "coupleId", "createdAt");
        updateById(period);
    }
    
    @Override
    public void delete(Long id) {
        Period period = getById(id);
        if (period == null || !period.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        removeById(id);
    }
    
    @Override
    public PeriodDTO predict() {
        Long userId = UserContext.getUserId();
        
        // 取最近6次记录
        LambdaQueryWrapper<Period> wrapper = new LambdaQueryWrapper<Period>()
                .eq(Period::getUserId, userId)
                .isNotNull(Period::getStartDate)
                .orderByDesc(Period::getStartDate)
                .last("LIMIT 6");
        
        List<Period> records = list(wrapper);
        
        PeriodDTO prediction = new PeriodDTO();
        
        if (records.isEmpty()) {
            // 无记录，返回默认值
            prediction.setAvgCycleLength(28);
            return prediction;
        }
        
        // 计算平均周期
        if (records.size() >= 2) {
            int totalCycleDays = 0;
            int count = 0;
            for (int i = 0; i < records.size() - 1; i++) {
                LocalDate current = records.get(i).getStartDate();
                LocalDate previous = records.get(i + 1).getStartDate();
                int days = (int) (current.toEpochDay() - previous.toEpochDay());
                if (days > 0 && days <= 60) {
                    totalCycleDays += days;
                    count++;
                }
            }
            int avgCycle = count > 0 ? totalCycleDays / count : 28;
            prediction.setAvgCycleLength(avgCycle);
            
            // 预测下次开始日期
            LocalDate lastStart = records.get(0).getStartDate();
            prediction.setNextPredictedDate(lastStart.plusDays(avgCycle));
        } else {
            // 只有1条记录，使用默认28天
            int cycle = records.get(0).getCycleLength() != null ? records.get(0).getCycleLength() : 28;
            prediction.setAvgCycleLength(cycle);
            prediction.setNextPredictedDate(records.get(0).getStartDate().plusDays(cycle));
        }
        
        return prediction;
    }
    
    private PeriodDTO toDTO(Period period) {
        PeriodDTO dto = new PeriodDTO();
        BeanUtil.copyProperties(period, dto);
        return dto;
    }
}
