package com.loveapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loveapp.common.Result;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.ImageDTO;
import com.loveapp.entity.Diary;
import com.loveapp.entity.DiaryImage;
import com.loveapp.entity.User;
import com.loveapp.mapper.DiaryImageMapper;
import com.loveapp.mapper.DiaryMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图片库控制器
 * 自动归集日记中的图片
 */
@Slf4j
@RestController
@RequestMapping("/gallery")
public class GalleryController {
    
    @Autowired
    private DiaryImageMapper diaryImageMapper;
    
    @Autowired
    private DiaryMapper diaryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取图片列表（按时间倒序，支持按月筛选）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        Long coupleId = getCoupleId();
        
        // 1. 查询该情侣空间下所有日记ID
        LambdaQueryWrapper<Diary> diaryWrapper = new LambdaQueryWrapper<Diary>()
                .eq(Diary::getCoupleId, coupleId)
                .select(Diary::getId);
        
        List<Long> diaryIds = diaryMapper.selectList(diaryWrapper)
                .stream().map(Diary::getId).collect(Collectors.toList());
        
        if (diaryIds.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("total", 0);
            data.put("list", Collections.emptyList());
            data.put("hasMore", false);
            return Result.success(data);
        }
        
        // 2. 查询这些日记下的图片
        LambdaQueryWrapper<DiaryImage> imageWrapper = new LambdaQueryWrapper<DiaryImage>()
                .in(DiaryImage::getDiaryId, diaryIds)
                .orderByDesc(DiaryImage::getCreatedAt);
        
        // 按月筛选
        if (year != null && month != null) {
            String startDate = String.format("%d-%02d-01 00:00:00", year, month);
            String endDate;
            if (month == 12) {
                endDate = String.format("%d-01-01 00:00:00", year + 1);
            } else {
                endDate = String.format("%d-%02d-01 00:00:00", year, month + 1);
            }
            imageWrapper.apply("created_at >= {0} AND created_at < {1}", startDate, endDate);
        }
        
        IPage<DiaryImage> pageResult = diaryImageMapper.selectPage(
                new Page<>(page, pageSize), imageWrapper);
        
        List<ImageDTO> images = pageResult.getRecords().stream().map(img -> {
            ImageDTO dto = new ImageDTO();
            dto.setId(img.getId());
            dto.setUrl(img.getUrl());
            dto.setThumbUrl(img.getThumbUrl());
            dto.setWidth(img.getWidth());
            dto.setHeight(img.getHeight());
            dto.setSource("diary");
            dto.setSourceId(img.getDiaryId());
            dto.setCreatedAt(img.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageResult.getTotal());
        data.put("list", images);
        data.put("hasMore", pageResult.getCurrent() < pageResult.getPages());
        
        return Result.success(data);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
}
