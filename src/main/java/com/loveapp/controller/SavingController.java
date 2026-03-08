package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.SavingDTO;
import com.loveapp.service.SavingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/saving")
public class SavingController {
    
    @Autowired
    private SavingService savingService;
    
    @GetMapping("/list")
    public Result<List<SavingDTO>> getList() {
        return Result.success(savingService.getList());
    }
    
    @GetMapping("/{id}")
    public Result<SavingDTO> getDetail(@PathVariable Long id) {
        return Result.success(savingService.getDetail(id));
    }
    
    @PostMapping
    public Result<Long> create(@RequestBody @Valid SavingDTO dto) {
        return Result.success(savingService.create(dto));
    }
    
    @PostMapping("/{id}/deposit")
    public Result<Void> deposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {
        savingService.deposit(id, amount, note);
        return Result.success();
    }
    
    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        savingService.complete(id);
        return Result.success();
    }
}
