package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.WishDTO;
import com.loveapp.service.WishService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wish")
public class WishController {
    
    @Autowired
    private WishService wishService;
    
    @GetMapping("/list")
    public Result<List<WishDTO>> getList(@RequestParam(required = false) Integer status) {
        return Result.success(wishService.getList(status));
    }
    
    @GetMapping("/{id}")
    public Result<WishDTO> getDetail(@PathVariable Long id) {
        return Result.success(wishService.getDetail(id));
    }
    
    @PostMapping
    public Result<Long> create(@RequestBody @Valid WishDTO dto) {
        return Result.success(wishService.create(dto));
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody @Valid WishDTO dto) {
        wishService.update(dto);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wishService.delete(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        wishService.updateStatus(id, status);
        return Result.success();
    }
}
