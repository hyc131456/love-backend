package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.PeriodDTO;
import com.loveapp.entity.Period;

import java.util.List;

/**
 * 姨妈期服务接口
 */
public interface PeriodService extends IService<Period> {
    
    List<PeriodDTO> getList();
    
    Long record(PeriodDTO dto);
    
    void update(PeriodDTO dto);
    
    void delete(Long id);
    
    PeriodDTO predict();
}
