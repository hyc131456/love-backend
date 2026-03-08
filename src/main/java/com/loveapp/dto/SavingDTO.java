package com.loveapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SavingDTO {
    
    private Long id;
    
    private String name;
    
    private String icon;
    
    private BigDecimal targetAmount;
    
    private BigDecimal currentAmount;
    
    private LocalDate deadline;
    
    private Long linkedWishId;
    
    private BigDecimal userAAmount;
    
    private BigDecimal userBAmount;
    
    private Integer status;
    
    private Integer progress;
    
    private LocalDateTime createdAt;
    
    private List<RecordDTO> records;
    
    @Data
    public static class RecordDTO {
        private Long id;
        private Long userId;
        private String userName;
        private BigDecimal amount;
        private String note;
        private LocalDateTime createdAt;
    }
}
