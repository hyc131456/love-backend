package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.CoupleDTO;
import com.loveapp.dto.CreateCoupleDTO;
import com.loveapp.dto.JoinCoupleDTO;
import com.loveapp.entity.Couple;

/**
 * 情侣空间服务接口
 */
public interface CoupleService extends IService<Couple> {
    
    /**
     * 创建情侣空间
     */
    CreateCoupleDTO createSpace();
    
    /**
     * 加入情侣空间
     */
    CoupleDTO joinSpace(JoinCoupleDTO dto);
    
    /**
     * 获取当前空间信息
     */
    CoupleDTO getCurrentCouple();
    
    /**
     * 获取伴侣信息
     */
    Object getPartner();
    
    /**
     * 生成新邀请码
     */
    String refreshInviteCode();
    
    /**
     * 解除配对
     */
    void unbind();
}
