package com.loveapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.loveapp.entity.Event;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日历事件 Mapper
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {
}
