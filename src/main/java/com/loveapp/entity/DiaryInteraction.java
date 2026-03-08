package com.loveapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日记互动（点赞、评论）实体类
 */
@Data
@TableName("diary_interactions")
public class DiaryInteraction {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 所属日记ID
     */
    private Long diaryId;
    
    /**
     * 互动用户ID
     */
    private Long userId;
    
    /**
     * 互动类型: like/comment
     */
    private String type;
    
    /**
     * 评论内容(点赞时可为空)
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
