package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.RecipeDTO;
import com.loveapp.entity.Recipe;
import com.loveapp.entity.User;
import com.loveapp.mapper.RecipeMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.service.AchievementService;
import com.loveapp.service.IntimacyService;
import com.loveapp.service.RecipeService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private IntimacyService intimacyService;
    
    @Autowired
    private AchievementService achievementService;
    
    @Override
    public List<RecipeDTO> getList(String category, Boolean favorite) {
        Long coupleId = getCoupleId();
        
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<Recipe>()
                .and(w -> w.eq(Recipe::getCoupleId, coupleId).or().eq(Recipe::getIsOfficial, 1))
                .orderByDesc(Recipe::getCreatedAt);
        
        if (category != null) {
            wrapper.eq(Recipe::getCategory, category);
        }
        if (Boolean.TRUE.equals(favorite)) {
            wrapper.eq(Recipe::getIsFavorite, 1);
        }
        
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public RecipeDTO getDetail(Long id) {
        Recipe recipe = getById(id);
        if (recipe == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return toDTO(recipe);
    }
    
    @Override
    public Long create(RecipeDTO dto) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Recipe recipe = new Recipe();
        recipe.setCoupleId(coupleId);
        recipe.setCreatorId(userId);
        recipe.setIsOfficial(0);
        recipe.setName(dto.getName());
        recipe.setCategory(dto.getCategory());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCookTime(dto.getCookTime());
        recipe.setCoverImage(dto.getCoverImage());
        recipe.setTips(dto.getTips());
        recipe.setTryCount(0);
        recipe.setIsFavorite(0);
        
        if (dto.getIngredients() != null) {
            recipe.setIngredients(dto.getIngredients());
        }
        if (dto.getSteps() != null) {
            recipe.setSteps(dto.getSteps());
        }
        
        save(recipe);
        
        // 创建菜谱成就检查
        achievementService.checkAndUnlock();
        
        return recipe.getId();
    }
    
    @Override
    public void update(RecipeDTO dto) {
        Recipe recipe = getById(dto.getId());
        if (recipe == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        recipe.setName(dto.getName());
        recipe.setCategory(dto.getCategory());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCookTime(dto.getCookTime());
        recipe.setCoverImage(dto.getCoverImage());
        recipe.setTips(dto.getTips());
        
        if (dto.getIngredients() != null) {
            recipe.setIngredients(dto.getIngredients());
        }
        if (dto.getSteps() != null) {
            recipe.setSteps(dto.getSteps());
        }
        
        updateById(recipe);
    }
    
    @Override
    public void delete(Long id) {
        Recipe recipe = getById(id);
        if (recipe == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        removeById(id);
    }
    
    @Override
    public void toggleFavorite(Long id) {
        Recipe recipe = getById(id);
        if (recipe == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        recipe.setIsFavorite(recipe.getIsFavorite() == 1 ? 0 : 1);
        updateById(recipe);
    }
    
    @Override
    public void recordTry(Long id) {
        Recipe recipe = getById(id);
        if (recipe == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        recipe.setTryCount(recipe.getTryCount() + 1);
        recipe.setLastTryDate(LocalDate.now());
        updateById(recipe);
        
        // 菜谱尝试积分
        intimacyService.addScore(IntimacyService.ACTION_RECIPE_TRY);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
    
    private RecipeDTO toDTO(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        BeanUtil.copyProperties(recipe, dto);
        dto.setIsFavorite(recipe.getIsFavorite() == 1);
        dto.setIsOfficial(recipe.getIsOfficial() == 1);
        
        // 删除多余手动转换
        return dto;
    }
}
