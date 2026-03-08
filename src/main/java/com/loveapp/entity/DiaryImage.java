package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 日记图片实体
 */
@Data
@TableName("diary_images")
public class DiaryImage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 日记ID */
    private Long diaryId;
    
    /** 图片URL */
    private String url;
    
    /** 缩略图URL */
    private String thumbUrl;
    
    /** 宽度 */
    private Integer width;
    
    /** 高度 */
    private Integer height;
    
    /** 排序 */
    private Integer sortOrder;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
