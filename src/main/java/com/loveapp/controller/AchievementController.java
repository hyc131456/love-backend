package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.AchievementDTO;
import com.loveapp.service.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/achievement")
public class AchievementController {
    
    @Autowired
    private AchievementService achievementService;
    
    @GetMapping("/unlocked")
    public Result<List<AchievementDTO>> getUnlockedList() {
        return Result.success(achievementService.getUnlockedList());
    }
    
    @GetMapping("/all")
    public Result<List<AchievementDTO>> getAllBadges() {
        return Result.success(achievementService.getAllBadges());
    }
    
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        achievementService.markAsRead(id);
        return Result.success();
    }
}
