package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片库DTO
 */
@Data
public class ImageDTO {
    
    private Long id;
    
    private String url;
    
    private String thumbUrl;
    
    /** 来源类型: diary / recipe */
    private String source;
    
    /** 来源ID */
    private Long sourceId;
    
    private Integer width;
    
    private Integer height;
    
    private LocalDateTime createdAt;
}
