package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.RecipeDTO;
import com.loveapp.entity.Recipe;

import java.util.List;

public interface RecipeService extends IService<Recipe> {
    
    List<RecipeDTO> getList(String category, Boolean favorite);
    
    RecipeDTO getDetail(Long id);
    
    Long create(RecipeDTO dto);
    
    void update(RecipeDTO dto);
    
    void delete(Long id);
    
    void toggleFavorite(Long id);
    
    void recordTry(Long id);
}
