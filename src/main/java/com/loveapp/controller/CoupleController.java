package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.CoupleDTO;
import com.loveapp.dto.CreateCoupleDTO;
import com.loveapp.dto.JoinCoupleDTO;
import com.loveapp.service.CoupleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 情侣空间控制器
 */
@Slf4j
@RestController
@RequestMapping("/couple")
public class CoupleController {
    
    @Autowired
    private CoupleService coupleService;
    
    /**
     * 创建情侣空间
     */
    @PostMapping("/create")
    public Result<CreateCoupleDTO> createSpace() {
        CreateCoupleDTO dto = coupleService.createSpace();
        return Result.success(dto);
    }
    
    /**
     * 加入情侣空间
     */
    @PostMapping("/join")
    public Result<CoupleDTO> joinSpace(@RequestBody @Valid JoinCoupleDTO dto) {
        CoupleDTO result = coupleService.joinSpace(dto);
        return Result.success(result);
    }
    
    /**
     * 获取当前空间信息
     */
    @GetMapping("/info")
    public Result<CoupleDTO> getCoupleInfo() {
        CoupleDTO dto = coupleService.getCurrentCouple();
        return Result.success(dto);
    }
    
    /**
     * 获取伴侣信息
     */
    @GetMapping("/partner")
    public Result<Object> getPartner() {
        Object partner = coupleService.getPartner();
        return Result.success(partner);
    }
    
    /**
     * 刷新邀请码
     */
    @PostMapping("/refreshCode")
    public Result<String> refreshInviteCode() {
        String code = coupleService.refreshInviteCode();
        return Result.success(code);
    }
    
    /**
     * 解除配对
     */
    @PostMapping("/unbind")
    public Result<Void> unbind() {
        coupleService.unbind();
        return Result.success();
    }
}
