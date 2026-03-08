package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.Wish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 心愿 Mapper
 */
@Mapper
public interface WishMapper extends BaseMapper<Wish> {
}
