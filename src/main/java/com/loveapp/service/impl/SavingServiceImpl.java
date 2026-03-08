package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.SavingDTO;
import com.loveapp.entity.Saving;
import com.loveapp.entity.SavingRecord;
import com.loveapp.entity.User;
import com.loveapp.mapper.SavingMapper;
import com.loveapp.mapper.SavingRecordMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.AchievementService;
import com.loveapp.service.IntimacyService;
import com.loveapp.service.SavingService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SavingServiceImpl extends ServiceImpl<SavingMapper, Saving> implements SavingService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SavingRecordMapper savingRecordMapper;
    
    @Autowired
    private IntimacyService intimacyService;
    
    @Autowired
    private AchievementService achievementService;
    
    @Override
    public List<SavingDTO> getList() {
        Long coupleId = getCoupleId();
        
        List<Saving> list = list(new LambdaQueryWrapper<Saving>()
                .eq(Saving::getCoupleId, coupleId)
                .orderByDesc(Saving::getCreatedAt));
        
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public SavingDTO getDetail(Long id) {
        Saving saving = getById(id);
        if (saving == null || !saving.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        SavingDTO dto = toDTO(saving);
        
        // 加载存款记录
        List<SavingRecord> records = savingRecordMapper.selectList(
                new LambdaQueryWrapper<SavingRecord>()
                        .eq(SavingRecord::getSavingId, id)
                        .orderByDesc(SavingRecord::getCreatedAt));
        
        List<SavingDTO.RecordDTO> recordDTOs = records.stream().map(r -> {
            SavingDTO.RecordDTO recordDTO = new SavingDTO.RecordDTO();
            recordDTO.setId(r.getId());
            recordDTO.setUserId(r.getUserId());
            recordDTO.setAmount(r.getAmount());
            recordDTO.setNote(r.getNote());
            recordDTO.setCreatedAt(r.getCreatedAt());
            
            User user = userMapper.selectById(r.getUserId());
            if (user != null) {
                recordDTO.setUserName(user.getNickname());
            }
            return recordDTO;
        }).collect(Collectors.toList());
        
        dto.setRecords(recordDTOs);
        return dto;
    }
    
    @Override
    public Long create(SavingDTO dto) {
        Long coupleId = getCoupleId();
        
        Saving saving = new Saving();
        BeanUtil.copyProperties(dto, saving);
        saving.setCoupleId(coupleId);
        saving.setCurrentAmount(BigDecimal.ZERO);
        saving.setUserAAmount(BigDecimal.ZERO);
        saving.setUserBAmount(BigDecimal.ZERO);
        saving.setStatus(0);
        
        if (saving.getIcon() == null) {
            saving.setIcon("💰");
        }
        
        save(saving);
        return saving.getId();
    }
    
    @Override
    @Transactional
    public void deposit(Long id, BigDecimal amount, String note) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        User user = userMapper.selectById(userId);
        
        Saving saving = getById(id);
        if (saving == null || !saving.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (saving.getStatus() != 0) {
            throw new BusinessException("该储蓄目标已完成或放弃");
        }
        
        // 更新储蓄金额
        saving.setCurrentAmount(saving.getCurrentAmount().add(amount));
        
        if ("A".equals(user.getRole())) {
            saving.setUserAAmount(saving.getUserAAmount().add(amount));
        } else {
            saving.setUserBAmount(saving.getUserBAmount().add(amount));
        }
        
        // 检查是否达成目标
        if (saving.getCurrentAmount().compareTo(saving.getTargetAmount()) >= 0) {
            saving.setStatus(1);
            saving.setCompletedTime(LocalDateTime.now());
        }
        
        updateById(saving);
        
        // 保存存款记录
        SavingRecord record = new SavingRecord();
        record.setSavingId(id);
        record.setCoupleId(coupleId);
        record.setUserId(userId);
        record.setAmount(amount);
        record.setNote(note);
        savingRecordMapper.insert(record);
        
        // 存款积分和成就检查
        intimacyService.addScore(IntimacyService.ACTION_SAVING);
        achievementService.checkAndUnlock();
    }
    
    @Override
    public void complete(Long id) {
        Saving saving = getById(id);
        if (saving == null || !saving.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        saving.setStatus(1);
        saving.setCompletedTime(LocalDateTime.now());
        updateById(saving);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
    
    private SavingDTO toDTO(Saving saving) {
        SavingDTO dto = new SavingDTO();
        BeanUtil.copyProperties(saving, dto);
        
        // 计算进度
        if (saving.getTargetAmount() != null && saving.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = saving.getCurrentAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(saving.getTargetAmount(), 0, RoundingMode.DOWN);
            dto.setProgress(progress.intValue());
        } else {
            dto.setProgress(0);
        }
        
        return dto;
    }
}
