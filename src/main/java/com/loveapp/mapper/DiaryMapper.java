package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.Diary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日记 Mapper
 */
@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
}
