package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.CoupleDTO;
import com.loveapp.dto.CreateCoupleDTO;
import com.loveapp.dto.JoinCoupleDTO;
import com.loveapp.dto.UserDTO;
import com.loveapp.entity.Couple;
import com.loveapp.entity.User;
import com.loveapp.mapper.CoupleMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.AchievementService;
import com.loveapp.service.CoupleService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 情侣空间服务实现
 */
@Slf4j
@Service
public class CoupleServiceImpl extends ServiceImpl<CoupleMapper, Couple> implements CoupleService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AchievementService achievementService;
    
    @Override
    @Transactional
    public CreateCoupleDTO createSpace() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        // 检查是否已有配对
        if (user.getCoupleId() != null) {
            Couple existingCouple = getById(user.getCoupleId());
            if (existingCouple != null && Couple.STATUS_WAITING == existingCouple.getStatus()) {
                log.info("用户 {} 正在请求创建空间，由于已处在 WAITING 状态空间 {}，直接返回既有信息", userId, existingCouple.getId());
                CreateCoupleDTO dto = new CreateCoupleDTO();
                dto.setCoupleId(existingCouple.getId());
                dto.setInviteCode(existingCouple.getInviteCode());
                if (existingCouple.getInviteCodeExpire() != null) {
                    dto.setExpireTime(existingCouple.getInviteCodeExpire().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
                return dto;
            }
            throw new BusinessException(ResultCode.ALREADY_COUPLED);
        }
        
        // 生成邀请码
        String inviteCode = generateInviteCode();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(24);
        
        // 创建空间
        Couple couple = new Couple();
        couple.setInviteCode(inviteCode);
        couple.setInviteCodeExpire(expireTime);
        couple.setUserA(userId);
        couple.setStatus(Couple.STATUS_WAITING);
        couple.setIntimacyScore(0);
        couple.setIntimacyLevel("热恋期");
        couple.setDailyScore(0);
        couple.setDiaryCount(0);
        couple.setEventCount(0);
        couple.setWishCompletedCount(0);
        save(couple);
        
        // 更新用户
        user.setCoupleId(couple.getId());
        user.setRole("A");
        userMapper.updateById(user);
        
        // 返回结果
        CreateCoupleDTO dto = new CreateCoupleDTO();
        dto.setCoupleId(couple.getId());
        dto.setInviteCode(inviteCode);
        dto.setExpireTime(expireTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return dto;
    }
    
    @Override
    @Transactional
    public CoupleDTO joinSpace(JoinCoupleDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        // 检查是否已有配对
        if (user.getCoupleId() != null) {
            throw new BusinessException(ResultCode.ALREADY_COUPLED);
        }
        
        // 查询邀请码
        Couple couple = getOne(new LambdaQueryWrapper<Couple>()
                .eq(Couple::getInviteCode, dto.getInviteCode().toUpperCase())
                .eq(Couple::getStatus, Couple.STATUS_WAITING));
        
        if (couple == null) {
            throw new BusinessException(ResultCode.INVITE_CODE_INVALID);
        }
        
        // 检查过期
        if (couple.getInviteCodeExpire() == null || couple.getInviteCodeExpire().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.INVITE_CODE_EXPIRED);
        }
        
        // 检查是否自己
        if (couple.getUserA().equals(userId)) {
            throw new BusinessException(ResultCode.CANNOT_COUPLE_SELF);
        }

        Long userAId = couple.getUserA();
        Long userBId = userId;

        // 查找是否存在历史解绑空间记录 (A-B 或 B-A)
        Couple oldCouple = getOne(new LambdaQueryWrapper<Couple>()
                .eq(Couple::getStatus, Couple.STATUS_UNBOUND)
                .and(w -> w.and(i -> i.eq(Couple::getUserA, userAId).eq(Couple::getUserB, userBId))
                        .or(i -> i.eq(Couple::getUserA, userBId).eq(Couple::getUserB, userAId))));

        if (oldCouple != null) {
            // 发现历史记录，复用/激活旧空间
            log.info("发现历史空间记录 {}, 执行激活逻辑...", oldCouple.getId());
            
            // 1. 激活旧空间
            oldCouple.setStatus(Couple.STATUS_COUPLED);
            oldCouple.setAnniversary(LocalDate.now());
            oldCouple.setUnbindTime(null);
            updateById(oldCouple);

            // 2. 更新当前用户 B
            user.setCoupleId(oldCouple.getId());
            user.setRole(oldCouple.getUserA().equals(userBId) ? "A" : "B");
            userMapper.updateById(user);

            // 3. 更新邀请者用户 A
            User userA = userMapper.selectById(userAId);
            userA.setCoupleId(oldCouple.getId());
            userA.setRole(oldCouple.getUserA().equals(userAId) ? "A" : "B");
            userMapper.updateById(userA);

            // 4. 删除本次为了邀请而生成的临时新空间记录
            removeById(couple.getId());
            
            // 配对成功，触发成就检查
            achievementService.checkAndUnlock();
            
            return buildCoupleDTO(oldCouple, userId);
        }

        // --- 正常配对逻辑 (原逻辑) ---
        // 配对
        this.update(new LambdaUpdateWrapper<Couple>()
                .set(Couple::getUserB, userId)
                .set(Couple::getStatus, Couple.STATUS_COUPLED)
                .set(Couple::getAnniversary, LocalDate.now())
                .set(Couple::getInviteCode, null)
                .set(Couple::getInviteCodeExpire, null)
                .eq(Couple::getId, couple.getId()));
        
        // 更新用户B
        user.setCoupleId(couple.getId());
        user.setRole("B");
        userMapper.updateById(user);
        
        // 配对成功，触发成就检查
        achievementService.checkAndUnlock();
        
        return buildCoupleDTO(couple, userId);
    }
    
    @Override
    public CoupleDTO getCurrentCouple() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        
        Couple couple = getById(user.getCoupleId());
        return buildCoupleDTO(couple, userId);
    }
    
    @Override
    public Object getPartner() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        
        Couple couple = getById(user.getCoupleId());
        Long partnerId = couple.getUserA().equals(userId) ? couple.getUserB() : couple.getUserA();
        
        if (partnerId == null) {
            return null;
        }
        
        User partner = userMapper.selectById(partnerId);
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(partner, dto);
        return dto;
    }
    
    @Override
    @Transactional
    public String refreshInviteCode() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        
        Couple couple = getById(user.getCoupleId());
        if (couple.getStatus() != Couple.STATUS_WAITING) {
            throw new BusinessException("已配对无法刷新邀请码");
        }
        
        String inviteCode = generateInviteCode();
        couple.setInviteCode(inviteCode);
        couple.setInviteCodeExpire(LocalDateTime.now().plusHours(24));
        updateById(couple);
        
        return inviteCode;
    }
    
    @Override
    @Transactional
    public void unbind() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        
        Couple couple = getById(user.getCoupleId());
        
        // 更新空间状态
        couple.setStatus(Couple.STATUS_UNBOUND);
        couple.setUnbindTime(LocalDateTime.now());
        updateById(couple);
        
        // 清除双方用户的配对信息
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .set(User::getCoupleId, null)
                .set(User::getRole, null)
                .eq(User::getId, couple.getUserA()));
        
        if (couple.getUserB() != null) {
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .set(User::getCoupleId, null)
                    .set(User::getRole, null)
                    .eq(User::getId, couple.getUserB()));
        }
    }
    
    private CoupleDTO buildCoupleDTO(Couple couple, Long userId) {
        CoupleDTO dto = new CoupleDTO();
        dto.setId(couple.getId());
        dto.setInviteCode(couple.getInviteCode());
        dto.setInviteCodeExpire(couple.getInviteCodeExpire());
        dto.setStatus(couple.getStatus());
        dto.setAnniversary(couple.getAnniversary());
        dto.setIntimacyLevel(couple.getIntimacyLevel());
        dto.setCreatedAt(couple.getCreatedAt());
        
        // 计算在一起天数
        if (couple.getAnniversary() != null) {
            dto.setDaysTogether(ChronoUnit.DAYS.between(couple.getAnniversary(), LocalDate.now()) + 1);
        }
        
        // 计算亲密值进度
        int score = couple.getIntimacyScore() != null ? couple.getIntimacyScore() : 0;
        dto.setIntimacyScore(score);
        int progress;
        if (score <= 100) {
            progress = score;
        } else if (score <= 300) {
            progress = (score - 100) * 100 / 200;
        } else if (score <= 500) {
            progress = (score - 300) * 100 / 200;
        } else {
            progress = 100;
        }
        dto.setIntimacyProgress(progress);
        
        // 获取伴侣信息
        Long partnerId = couple.getUserA().equals(userId) ? couple.getUserB() : couple.getUserA();
        if (partnerId != null) {
            User partner = userMapper.selectById(partnerId);
            if (partner != null) {
                UserDTO partnerDTO = new UserDTO();
                BeanUtil.copyProperties(partner, partnerDTO);
                dto.setPartner(partnerDTO);
            }
        }
        
        return dto;
    }
    
    private String generateInviteCode() {
        String code;
        int attempts = 0;
        do {
            code = RandomUtil.randomString("ABCDEFGHJKLMNPQRSTUVWXYZ23456789", 6);
            attempts++;
        } while (isCodeExists(code) && attempts < 10);
        return code;
    }
    
    private boolean isCodeExists(String code) {
        return count(new LambdaQueryWrapper<Couple>()
                .eq(Couple::getInviteCode, code)
                .eq(Couple::getStatus, Couple.STATUS_WAITING)) > 0;
    }
}
