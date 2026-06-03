-- 为日历事件增加当天内手动排序字段。
-- 现有环境执行一次即可；新环境使用 db/init.sql 不需要执行本脚本。

ALTER TABLE `events`
    ADD COLUMN `sort_order` INT DEFAULT 0 COMMENT '当天事件排序' AFTER `creator_id`;

UPDATE `events` e
JOIN (
    SELECT
        `id`,
        ROW_NUMBER() OVER (
            PARTITION BY `couple_id`, `event_date`
            ORDER BY `event_time`, `id`
        ) - 1 AS `next_sort_order`
    FROM `events`
    WHERE `deleted` = 0
) ordered_events ON ordered_events.`id` = e.`id`
SET e.`sort_order` = ordered_events.`next_sort_order`;
