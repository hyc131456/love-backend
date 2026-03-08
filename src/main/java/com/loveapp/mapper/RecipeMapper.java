package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}
