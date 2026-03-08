package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.Couple;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 情侣空间 Mapper
 */
@Mapper
public interface CoupleMapper extends BaseMapper<Couple> {
    
    /**
     * 根据邀请码查询
     */
    Couple selectByInviteCode(@Param("inviteCode") String inviteCode);
}
