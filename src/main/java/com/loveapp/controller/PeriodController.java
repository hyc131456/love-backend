package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.PeriodDTO;
import com.loveapp.service.PeriodService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 姨妈期控制器
 */
@Slf4j
@RestController
@RequestMapping("/period")
public class PeriodController {
    
    @Autowired
    private PeriodService periodService;
    
    /**
     * 获取经期记录列表
     */
    @GetMapping("/list")
    public Result<List<PeriodDTO>> getList() {
        return Result.success(periodService.getList());
    }
    
    /**
     * 记录经期
     */
    @PostMapping
    public Result<Long> record(@RequestBody @Valid PeriodDTO dto) {
        return Result.success(periodService.record(dto));
    }
    
    /**
     * 更新经期记录
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid PeriodDTO dto) {
        periodService.update(dto);
        return Result.success();
    }
    
    /**
     * 删除经期记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        periodService.delete(id);
        return Result.success();
    }
    
    /**
     * 预测下次经期
     */
    @GetMapping("/predict")
    public Result<PeriodDTO> predict() {
        return Result.success(periodService.predict());
    }
}
