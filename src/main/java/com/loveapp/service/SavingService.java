package com.loveapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.loveapp.dto.SavingDTO;
import com.loveapp.entity.Saving;

import java.math.BigDecimal;
import java.util.List;

public interface SavingService extends IService<Saving> {
    
    List<SavingDTO> getList();
    
    SavingDTO getDetail(Long id);
    
    Long create(SavingDTO dto);
    
    void deposit(Long id, BigDecimal amount, String note);
    
    void complete(Long id);
}
