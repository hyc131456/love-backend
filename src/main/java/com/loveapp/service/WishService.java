package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.WishDTO;
import com.loveapp.entity.Wish;

import java.util.List;

/**
 * 心愿服务接口
 */
public interface WishService extends IService<Wish> {
    
    List<WishDTO> getList(Integer status);
    
    WishDTO getDetail(Long id);
    
    Long create(WishDTO dto);
    
    void update(WishDTO dto);
    
    void delete(Long id);
    
    void updateStatus(Long id, Integer status);
}
