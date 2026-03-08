package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.RecipeDTO;
import com.loveapp.service.RecipeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recipe")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    @GetMapping("/list")
    public Result<List<RecipeDTO>> getList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean favorite) {
        return Result.success(recipeService.getList(category, favorite));
    }
    
    @GetMapping("/{id}")
    public Result<RecipeDTO> getDetail(@PathVariable Long id) {
        return Result.success(recipeService.getDetail(id));
    }
    
    @PostMapping
    public Result<Long> create(@RequestBody @Valid RecipeDTO dto) {
        return Result.success(recipeService.create(dto));
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody @Valid RecipeDTO dto) {
        recipeService.update(dto);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id) {
        recipeService.toggleFavorite(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/try")
    public Result<Void> recordTry(@PathVariable Long id) {
        recipeService.recordTry(id);
        return Result.success();
    }
}
