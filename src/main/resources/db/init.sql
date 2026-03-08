-- =========================================
-- 心迹·情侣时光 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- =========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_app 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE love_app;

-- =========================================
-- 1. 用户表
-- =========================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `password` VARCHAR(64) DEFAULT NULL COMMENT '密码（MD5加密）',
    `openid` VARCHAR(64) DEFAULT NULL COMMENT '微信OpenID',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信UnionID',
    `couple_id` BIGINT DEFAULT NULL COMMENT '关联的情侣空间ID',
    `role` CHAR(1) DEFAULT NULL COMMENT '在情侣空间中的角色: A/B',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `enable_period` TINYINT DEFAULT 0 COMMENT '是否开启姨妈期模块',
    `share_period` TINYINT DEFAULT 0 COMMENT '是否共享姨妈期给伴侣',
    `enable_notification` TINYINT DEFAULT 1 COMMENT '是否开启通知',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_couple_id` (`couple_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =========================================
-- 2. 情侣空间表
-- =========================================
DROP TABLE IF EXISTS `couples`;
CREATE TABLE `couples` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '空间ID',
    `invite_code` VARCHAR(10) DEFAULT NULL COMMENT '邀请码',
    `invite_code_expire` DATETIME DEFAULT NULL COMMENT '邀请码过期时间',
    `user_a` BIGINT NOT NULL COMMENT '创建者用户ID',
    `user_b` BIGINT DEFAULT NULL COMMENT '加入者用户ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待配对 1已配对 2已解绑',
    `anniversary` DATE DEFAULT NULL COMMENT '恋爱纪念日',
    `intimacy_score` INT DEFAULT 0 COMMENT '亲密值总分',
    `intimacy_level` VARCHAR(20) DEFAULT '热恋期' COMMENT '亲密等级',
    `daily_score` INT DEFAULT 0 COMMENT '今日已获积分',
    `last_score_date` DATE DEFAULT NULL COMMENT '上次积分日期',
    `diary_count` INT DEFAULT 0 COMMENT '日记总数',
    `event_count` INT DEFAULT 0 COMMENT '事件总数',
    `wish_completed_count` INT DEFAULT 0 COMMENT '已完成心愿数',
    `unbind_time` DATETIME DEFAULT NULL COMMENT '解绑时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_invite_code` (`invite_code`),
    KEY `idx_user_a` (`user_a`),
    KEY `idx_user_b` (`user_b`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情侣空间表';

-- =========================================
-- 3. 日历事件表
-- =========================================
DROP TABLE IF EXISTS `events`;
CREATE TABLE `events` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事件ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `type` VARCHAR(20) NOT NULL COMMENT '事件类型: anniversary/todo/diary/recipe/period',
    `title` VARCHAR(100) NOT NULL COMMENT '事件标题',
    `event_date` DATE NOT NULL COMMENT '事件日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `is_all_day` TINYINT DEFAULT 1 COMMENT '是否全天事件',
    `event_time` VARCHAR(10) DEFAULT NULL COMMENT '时间 HH:mm',
    `repeat_type` VARCHAR(20) DEFAULT 'none' COMMENT '重复类型: none/daily/weekly/monthly/yearly',
    `repeat_end_date` DATE DEFAULT NULL COMMENT '重复结束日期',
    `reminders` VARCHAR(200) DEFAULT NULL COMMENT '提醒设置JSON',
    `color` VARCHAR(10) DEFAULT NULL COMMENT '颜色代码',
    `icon` VARCHAR(20) DEFAULT NULL COMMENT '图标',
    `note` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `is_lunar` TINYINT DEFAULT 0 COMMENT '是否农历',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联的日记/菜谱ID',
    `creator_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0正常 1已完成 2已删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_couple_date` (`couple_id`, `event_date`),
    KEY `idx_couple_type` (`couple_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日历事件表';

-- =========================================
-- 4. 日记表
-- =========================================
DROP TABLE IF EXISTS `diaries`;
CREATE TABLE `diaries` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `author_id` BIGINT NOT NULL COMMENT '作者用户ID',
    `content` TEXT COMMENT '日记内容',
    `mood` VARCHAR(20) DEFAULT NULL COMMENT '心情: happy/love/sad/angry/miss/tired',
    `weather` VARCHAR(20) DEFAULT NULL COMMENT '天气: sunny/cloudy/rainy/snowy',
    `location_name` VARCHAR(100) DEFAULT NULL COMMENT '地点名称',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
    `is_public` TINYINT DEFAULT 1 COMMENT '是否公开给伴侣',
    `is_draft` TINYINT DEFAULT 0 COMMENT '是否草稿',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_couple_time` (`couple_id`, `created_at` DESC),
    KEY `idx_author` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记表';

-- =========================================
-- 5. 日记图片表
-- =========================================
DROP TABLE IF EXISTS `diary_images`;
CREATE TABLE `diary_images` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `diary_id` BIGINT NOT NULL COMMENT '日记ID',
    `url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `thumb_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
    `width` INT DEFAULT NULL COMMENT '宽度',
    `height` INT DEFAULT NULL COMMENT '高度',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_diary` (`diary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记图片表';

-- =========================================
-- 6. 日记互动表（点赞、评论）
-- =========================================
DROP TABLE IF EXISTS `diary_interactions`;
CREATE TABLE `diary_interactions` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `diary_id` BIGINT NOT NULL COMMENT '日记ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(10) NOT NULL COMMENT '类型: like/comment',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '评论内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_diary` (`diary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记互动表';

-- =========================================
-- 7. 心愿清单表
-- =========================================
DROP TABLE IF EXISTS `wishes`;
CREATE TABLE `wishes` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '心愿ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `title` VARCHAR(100) NOT NULL COMMENT '心愿标题',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '详细描述',
    `category` VARCHAR(20) DEFAULT 'other' COMMENT '类型: travel/gift/experience/home/study/health/other',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `budget` DECIMAL(10,2) DEFAULT NULL COMMENT '预算金额',
    `target_date` DATE DEFAULT NULL COMMENT '期望完成日期',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0未开始 1进行中 2已完成 3已放弃',
    `linked_saving_id` BIGINT DEFAULT NULL COMMENT '关联的储蓄目标ID',
    `creator_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `completed_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_couple_status` (`couple_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心愿清单表';

-- =========================================
-- 8. 心愿助力表
-- =========================================
DROP TABLE IF EXISTS `wish_supports`;
CREATE TABLE `wish_supports` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `wish_id` BIGINT NOT NULL COMMENT '心愿ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `message` VARCHAR(200) DEFAULT NULL COMMENT '留言',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_wish` (`wish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心愿助力表';

-- =========================================
-- 9. 储蓄目标表
-- =========================================
DROP TABLE IF EXISTS `savings`;
CREATE TABLE `savings` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '储蓄ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `name` VARCHAR(50) NOT NULL COMMENT '目标名称',
    `icon` VARCHAR(20) DEFAULT '💰' COMMENT '图标',
    `target_amount` DECIMAL(12,2) NOT NULL COMMENT '目标金额',
    `current_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '当前金额',
    `deadline` DATE DEFAULT NULL COMMENT '截止日期',
    `linked_wish_id` BIGINT DEFAULT NULL COMMENT '关联的心愿ID',
    `user_a_amount` DECIMAL(12,2) DEFAULT 0 COMMENT 'A的贡献金额',
    `user_b_amount` DECIMAL(12,2) DEFAULT 0 COMMENT 'B的贡献金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0进行中 1已完成 2已放弃',
    `completed_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_couple` (`couple_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='储蓄目标表';

-- =========================================
-- 10. 存款记录表
-- =========================================
DROP TABLE IF EXISTS `saving_records`;
CREATE TABLE `saving_records` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `saving_id` BIGINT NOT NULL COMMENT '储蓄目标ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `user_id` BIGINT NOT NULL COMMENT '存款人用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '存款金额',
    `note` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_saving` (`saving_id`, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存款记录表';

-- =========================================
-- 11. 成就记录表
-- =========================================
DROP TABLE IF EXISTS `achievements`;
CREATE TABLE `achievements` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `couple_id` BIGINT NOT NULL COMMENT '情侣空间ID',
    `badge_id` VARCHAR(50) NOT NULL COMMENT '徽章ID',
    `badge_name` VARCHAR(50) NOT NULL COMMENT '徽章名称',
    `unlocked_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
    `is_new` TINYINT DEFAULT 1 COMMENT '是否新解锁',
    UNIQUE KEY `uk_couple_badge` (`couple_id`, `badge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成就记录表';

-- =========================================
-- 12. 菜谱表
-- =========================================
DROP TABLE IF EXISTS `recipes`;
CREATE TABLE `recipes` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜谱ID',
    `couple_id` BIGINT DEFAULT NULL COMMENT '情侣空间ID（官方菜谱为空）',
    `is_official` TINYINT DEFAULT 0 COMMENT '是否官方菜谱',
    `name` VARCHAR(50) NOT NULL COMMENT '菜名',
    `category` VARCHAR(20) DEFAULT 'staple' COMMENT '分类: staple/cold/soup/dessert/drink',
    `difficulty` TINYINT DEFAULT 1 COMMENT '难度: 1简单 2中等 3困难',
    `cook_time` INT DEFAULT NULL COMMENT '烹饪时间（分钟）',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `ingredients` TEXT COMMENT '食材列表JSON',
    `steps` TEXT COMMENT '步骤JSON',
    `tips` VARCHAR(500) DEFAULT NULL COMMENT '小贴士',
    `try_count` INT DEFAULT 0 COMMENT '尝试次数',
    `last_try_date` DATE DEFAULT NULL COMMENT '最后尝试日期',
    `is_favorite` TINYINT DEFAULT 0 COMMENT '是否收藏',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    KEY `idx_couple` (`couple_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜谱表';

-- =========================================
-- 13. 姨妈期记录表
-- =========================================
DROP TABLE IF EXISTS `periods`;
CREATE TABLE `periods` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `couple_id` BIGINT DEFAULT NULL COMMENT '情侣空间ID',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `cycle_length` INT DEFAULT 28 COMMENT '周期长度（天）',
    `period_length` INT DEFAULT 5 COMMENT '经期长度（天）',
    `flow` VARCHAR(10) DEFAULT 'normal' COMMENT '流量: light/normal/heavy',
    `symptoms` VARCHAR(200) DEFAULT NULL COMMENT '症状列表JSON',
    `note` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_user_date` (`user_id`, `start_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姨妈期记录表';

-- =========================================
-- 完成
-- =========================================
SELECT '数据库初始化完成！' AS message;
