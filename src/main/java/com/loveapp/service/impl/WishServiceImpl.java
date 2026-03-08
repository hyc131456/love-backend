package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.WishDTO;
import com.loveapp.entity.User;
import com.loveapp.entity.Wish;
import com.loveapp.mapper.UserMapper;
import com.loveapp.mapper.WishMapper;
import com.loveapp.service.AchievementService;
import com.loveapp.service.WishService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WishServiceImpl extends ServiceImpl<WishMapper, Wish> implements WishService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AchievementService achievementService;
    
    @Override
    public List<WishDTO> getList(Integer status) {
        Long coupleId = getCoupleId();
        
        LambdaQueryWrapper<Wish> wrapper = new LambdaQueryWrapper<Wish>()
                .eq(Wish::getCoupleId, coupleId)
                .orderByDesc(Wish::getCreatedAt);
        
        if (status != null) {
            if (status == 2) {
                wrapper.eq(Wish::getStatus, 2);
            } else {
                wrapper.in(Wish::getStatus, 0, 1);
            }
        }
        
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public WishDTO getDetail(Long id) {
        Wish wish = getById(id);
        if (wish == null || !wish.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return toDTO(wish);
    }
    
    @Override
    public Long create(WishDTO dto) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Wish wish = new Wish();
        BeanUtil.copyProperties(dto, wish);
        wish.setCoupleId(coupleId);
        wish.setCreatorId(userId);
        wish.setStatus(0);
        
        save(wish);
        
        // 创建心愿成就检查
        achievementService.checkAndUnlock();
        
        return wish.getId();
    }
    
    @Override
    public void update(WishDTO dto) {
        Wish wish = getById(dto.getId());
        if (wish == null || !wish.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        BeanUtil.copyProperties(dto, wish, "id", "coupleId", "creatorId", "createdAt");
        updateById(wish);
    }
    
    @Override
    public void delete(Long id) {
        Wish wish = getById(id);
        if (wish == null || !wish.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        removeById(id);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        Wish wish = getById(id);
        if (wish == null || !wish.getCoupleId().equals(getCoupleId())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        wish.setStatus(status);
        if (status == 2) {
            wish.setCompletedTime(LocalDateTime.now());
        }
        updateById(wish);
        
        // 状态变更成就检查
        achievementService.checkAndUnlock();
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
    
    private WishDTO toDTO(Wish wish) {
        WishDTO dto = new WishDTO();
        BeanUtil.copyProperties(wish, dto);
        return dto;
    }
}
