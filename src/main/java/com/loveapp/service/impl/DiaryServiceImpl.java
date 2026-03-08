package com.loveapp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.dto.DiaryDTO;
import com.loveapp.dto.UserDTO;
import com.loveapp.entity.Diary;
import com.loveapp.entity.DiaryImage;
import com.loveapp.entity.User;
import com.loveapp.mapper.DiaryImageMapper;
import com.loveapp.mapper.DiaryInteractionMapper;
import com.loveapp.mapper.DiaryMapper;
import com.loveapp.mapper.UserMapper;
import com.loveapp.entity.DiaryInteraction;
import java.time.LocalDateTime;
import com.loveapp.service.AchievementService;
import com.loveapp.service.DiaryService;
import com.loveapp.service.IntimacyService;
import com.loveapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 日记服务实现
 */
@Slf4j
@Service
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements DiaryService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private DiaryImageMapper diaryImageMapper;
    
    @Autowired
    private DiaryInteractionMapper diaryInteractionMapper;
    
    @Autowired
    private IntimacyService intimacyService;
    
    @Autowired
    private AchievementService achievementService;
    
    @Override
    public IPage<DiaryDTO> getList(int page, int pageSize) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Page<Diary> pageParam = new Page<>(page, pageSize);
        
        IPage<Diary> diaryPage = page(pageParam, new LambdaQueryWrapper<Diary>()
                .eq(Diary::getCoupleId, coupleId)
                .eq(Diary::getIsPublic, 1)
                .eq(Diary::getIsDraft, 0)
                .orderByDesc(Diary::getCreatedAt));
        
        IPage<DiaryDTO> result = diaryPage.convert(diary -> toDTO(diary, userId));
        return result;
    }
    
    @Override
    public DiaryDTO getDetail(Long id) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Diary diary = getById(id);
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        return toDTO(diary, userId);
    }
    
    @Override
    @Transactional
    public Long create(DiaryDTO dto) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Diary diary = new Diary();
        BeanUtil.copyProperties(dto, diary, "id", "images", "author");
        diary.setCoupleId(coupleId);
        diary.setAuthorId(userId);
        diary.setLikeCount(0);
        diary.setCommentCount(0);
        
        if (diary.getIsPublic() == null) {
            diary.setIsPublic(1);
        }
        if (diary.getIsDraft() == null) {
            diary.setIsDraft(0);
        }
        
        save(diary);
        
        // 保存图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            int order = 0;
            for (DiaryDTO.DiaryImageDTO imgDto : dto.getImages()) {
                DiaryImage image = new DiaryImage();
                image.setDiaryId(diary.getId());
                image.setUrl(imgDto.getUrl());
                image.setThumbUrl(imgDto.getThumbUrl());
                image.setWidth(imgDto.getWidth());
                image.setHeight(imgDto.getHeight());
                image.setSortOrder(order++);
                diaryImageMapper.insert(image);
            }
        }
        
        // 触发亲密值积分和成就检查
        intimacyService.addScore(IntimacyService.ACTION_DIARY);
        achievementService.checkAndUnlock();
        
        return diary.getId();
    }
    
    @Override
    @Transactional
    public void update(DiaryDTO dto) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Diary diary = getById(dto.getId());
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        // 只有作者可以编辑
        if (!diary.getAuthorId().equals(userId)) {
            throw new BusinessException("只有作者可以编辑日记");
        }
        
        BeanUtil.copyProperties(dto, diary, "id", "images", "author", "coupleId", "authorId");
        updateById(diary);
        
        // 更新图片（先删后增）
        if (dto.getImages() != null) {
            diaryImageMapper.delete(new LambdaQueryWrapper<DiaryImage>()
                    .eq(DiaryImage::getDiaryId, diary.getId()));
            
            int order = 0;
            for (DiaryDTO.DiaryImageDTO imgDto : dto.getImages()) {
                DiaryImage image = new DiaryImage();
                image.setDiaryId(diary.getId());
                image.setUrl(imgDto.getUrl());
                image.setThumbUrl(imgDto.getThumbUrl());
                image.setWidth(imgDto.getWidth());
                image.setHeight(imgDto.getHeight());
                image.setSortOrder(order++);
                diaryImageMapper.insert(image);
            }
        }
    }
    
    @Override
    public void delete(Long id) {
        Long userId = UserContext.getUserId();
        Long coupleId = getCoupleId();
        
        Diary diary = getById(id);
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        // 只有作者可以删除
        if (!diary.getAuthorId().equals(userId)) {
            throw new BusinessException("只有作者可以删除日记");
        }
        
        removeById(id);
    }
    
    @Override
    public void like(Long id) {
        Long coupleId = getCoupleId();
        
        Diary diary = getById(id);
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        diary.setLikeCount(diary.getLikeCount() + 1);
        updateById(diary);
        
        // 互赞积分
        intimacyService.addScore(IntimacyService.ACTION_LIKE_COMMENT);
    }
    
    @Override
    public void unlike(Long id) {
        Long coupleId = getCoupleId();
        
        Diary diary = getById(id);
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        diary.setLikeCount(Math.max(0, diary.getLikeCount() - 1));
        updateById(diary);
    }
    
    @Override
    public void comment(Long id, String content) {
        Long coupleId = getCoupleId();
        Long userId = UserContext.getUserId();
        
        Diary diary = getById(id);
        if (diary == null || !diary.getCoupleId().equals(coupleId)) {
            throw new BusinessException(ResultCode.DIARY_NOT_FOUND);
        }
        
        diary.setCommentCount(diary.getCommentCount() + 1);
        updateById(diary);
        
        // 评论积分
        intimacyService.addScore(IntimacyService.ACTION_LIKE_COMMENT);
        
        // 保存评论到diary_interactions表
        DiaryInteraction interaction = new DiaryInteraction();
        interaction.setDiaryId(id);
        interaction.setUserId(userId);
        interaction.setType("comment");
        interaction.setContent(content);
        interaction.setCreatedAt(LocalDateTime.now());
        diaryInteractionMapper.insert(interaction);
    }
    
    private Long getCoupleId() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getCoupleId() == null) {
            throw new BusinessException(ResultCode.NOT_COUPLED);
        }
        return user.getCoupleId();
    }
    
    private DiaryDTO toDTO(Diary diary, Long currentUserId) {
        DiaryDTO dto = new DiaryDTO();
        BeanUtil.copyProperties(diary, dto);
        
        // 作者信息
        User author = userMapper.selectById(diary.getAuthorId());
        if (author != null) {
            UserDTO authorDTO = new UserDTO();
            authorDTO.setId(author.getId());
            authorDTO.setNickname(author.getNickname());
            authorDTO.setAvatar(author.getAvatar());
            dto.setAuthor(authorDTO);
        }
        
        // 图片列表
        List<DiaryImage> images = diaryImageMapper.selectList(
                new LambdaQueryWrapper<DiaryImage>()
                        .eq(DiaryImage::getDiaryId, diary.getId())
                        .orderByAsc(DiaryImage::getSortOrder));
        
        List<DiaryDTO.DiaryImageDTO> imageDTOs = images.stream().map(img -> {
            DiaryDTO.DiaryImageDTO imgDTO = new DiaryDTO.DiaryImageDTO();
            imgDTO.setUrl(img.getUrl());
            imgDTO.setThumbUrl(img.getThumbUrl());
            imgDTO.setWidth(img.getWidth());
            imgDTO.setHeight(img.getHeight());
            return imgDTO;
        }).collect(Collectors.toList());
        dto.setImages(imageDTOs);
        
        // 获取评论列表
        List<DiaryInteraction> comments = diaryInteractionMapper.selectList(
                new LambdaQueryWrapper<DiaryInteraction>()
                        .eq(DiaryInteraction::getDiaryId, diary.getId())
                        .eq(DiaryInteraction::getType, "comment")
                        .orderByAsc(DiaryInteraction::getCreatedAt));
                        
        List<DiaryDTO.CommentDTO> commentDTOs = comments.stream().map(comment -> {
            DiaryDTO.CommentDTO c = new DiaryDTO.CommentDTO();
            c.setId(comment.getId());
            c.setContent(comment.getContent());
            c.setCreatedAt(comment.getCreatedAt());
            
            User cAuthor = userMapper.selectById(comment.getUserId());
            if (cAuthor != null) {
                UserDTO caDTO = new UserDTO();
                caDTO.setId(cAuthor.getId());
                caDTO.setNickname(cAuthor.getNickname());
                caDTO.setAvatar(cAuthor.getAvatar());
                c.setAuthor(caDTO);
            }
            return c;
        }).collect(Collectors.toList());
        dto.setComments(commentDTOs);
        
        // 是否已点赞（简化实现）
        dto.setIsLiked(false);
        
        return dto;
    }
}
