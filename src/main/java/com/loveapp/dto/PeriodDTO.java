package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 姨妈期DTO
 */
@Data
public class PeriodDTO {
    
    private Long id;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer cycleLength;
    
    private Integer periodLength;
    
    private String flow;
    
    private String symptoms;
    
    private String note;
    
    /** 预测下次开始日期 */
    private LocalDate nextPredictedDate;
    
    /** 平均周期 */
    private Integer avgCycleLength;
    
    private LocalDateTime createdAt;
}
