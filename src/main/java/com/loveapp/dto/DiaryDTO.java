package com.loveapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记DTO
 */
@Data
public class DiaryDTO {
    
    private Long id;
    
    /** 作者信息 */
    private UserDTO author;
    
    /** 内容 */
    private String content;
    
    /** 图片列表 */
    private List<DiaryImageDTO> images;
    
    /** 心情 */
    private String mood;
    
    /** 天气 */
    private String weather;
    
    /** 位置名称 */
    private String locationName;
    
    /** 纬度 */
    private BigDecimal latitude;
    
    /** 经度 */
    private BigDecimal longitude;
    
    /** 是否公开 */
    private Integer isPublic;
    
    /** 是否草稿 */
    private Integer isDraft;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 是否已点赞 */
    private Boolean isLiked;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 评论列表 */
    private List<CommentDTO> comments;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /**
     * 日记图片DTO
     */
    @Data
    public static class DiaryImageDTO {
        private String url;
        private String thumbUrl;
        private Integer width;
        private Integer height;
    }

    /**
     * 日记评论DTO
     */
    @Data
    public static class CommentDTO {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private UserDTO author;
    }
}
