package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 日记实体
 */
@Data
@TableName("diaries")
public class Diary {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 情侣空间ID */
    private Long coupleId;
    
    /** 作者用户ID */
    private Long authorId;
    
    /** 日记内容 */
    private String content;
    
    /** 心情: happy/love/sad/angry/miss/tired */
    private String mood;
    
    /** 天气: sunny/cloudy/rainy/snowy */
    private String weather;
    
    /** 地点名称 */
    private String locationName;
    
    /** 纬度 */
    private BigDecimal latitude;
    
    /** 经度 */
    private BigDecimal longitude;
    
    /** 是否公开给伴侣 */
    private Integer isPublic;
    
    /** 是否草稿 */
    private Integer isDraft;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    @TableLogic
    private Integer deleted;
}
