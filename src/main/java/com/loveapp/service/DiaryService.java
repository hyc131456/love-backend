package com.loveapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.DiaryDTO;
import com.loveapp.entity.Diary;

/**
 * 日记服务接口
 */
public interface DiaryService extends IService<Diary> {
    
    /**
     * 获取日记列表
     */
    IPage<DiaryDTO> getList(int page, int pageSize);
    
    /**
     * 获取日记详情
     */
    DiaryDTO getDetail(Long id);
    
    /**
     * 创建日记
     */
    Long create(DiaryDTO dto);
    
    /**
     * 更新日记
     */
    void update(DiaryDTO dto);
    
    /**
     * 删除日记
     */
    void delete(Long id);
    
    /**
     * 点赞
     */
    void like(Long id);
    
    /**
     * 取消点赞
     */
    void unlike(Long id);
    
    /**
     * 评论
     */
    void comment(Long id, String content);
}
