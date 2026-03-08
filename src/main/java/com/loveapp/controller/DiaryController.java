package com.loveapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.loveapp.common.Result;
import com.loveapp.dto.DiaryDTO;
import com.loveapp.service.DiaryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 日记控制器
 */
@Slf4j
@RestController
@RequestMapping("/diary")
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;
    
    /**
     * 获取日记列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<DiaryDTO> pageResult = diaryService.getList(page, pageSize);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageResult.getTotal());
        data.put("list", pageResult.getRecords());
        data.put("hasMore", pageResult.getCurrent() < pageResult.getPages());
        
        return Result.success(data);
    }
    
    /**
     * 获取日记详情
     */
    @GetMapping("/{id}")
    public Result<DiaryDTO> getDetail(@PathVariable Long id) {
        DiaryDTO dto = diaryService.getDetail(id);
        return Result.success(dto);
    }
    
    /**
     * 创建日记
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid DiaryDTO dto) {
        Long id = diaryService.create(dto);
        return Result.success(id);
    }
    
    /**
     * 更新日记
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid DiaryDTO dto) {
        diaryService.update(dto);
        return Result.success();
    }
    
    /**
     * 删除日记
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        diaryService.delete(id);
        return Result.success();
    }
    
    /**
     * 点赞
     */
    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        diaryService.like(id);
        return Result.success();
    }
    
    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id) {
        diaryService.unlike(id);
        return Result.success();
    }
    
    /**
     * 评论
     */
    @PostMapping("/{id}/comment")
    public Result<Void> comment(
            @PathVariable Long id,
            @RequestParam String content) {
        diaryService.comment(id, content);
        return Result.success();
    }
}
