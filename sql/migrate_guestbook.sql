-- 留言板表迁移（已有库执行）
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `blog_guestbook` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父留言ID（0为顶级）',
  `author_name` varchar(100) NOT NULL COMMENT '昵称',
  `author_email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `author_avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `content` varchar(500) NOT NULL COMMENT '留言内容',
  `likes` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` varchar(20) NOT NULL DEFAULT 'APPROVED' COMMENT 'PENDING/APPROVED/SPAM',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP',
  `user_agent` varchar(500) DEFAULT NULL COMMENT 'UA',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言板';

INSERT INTO `blog_nav_item` (`name`, `path`, `icon`, `parent_id`, `sort_order`, `is_external`)
SELECT '留言板', '/guestbook', 'fa-solid fa-comments', 0, 6, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `blog_nav_item` WHERE `path` = '/guestbook');

UPDATE `blog_nav_item` SET `sort_order` = 7 WHERE `path` = '/lab' AND `sort_order` = 6;
UPDATE `blog_nav_item` SET `sort_order` = 8 WHERE `path` = '/about' AND `sort_order` = 7;

INSERT INTO `blog_site_setting` (`setting_key`, `setting_value`, `description`)
SELECT 'about_tech_stack', 'Java,Spring Boot,MySQL,Redis,Vue 3,Docker,Linux,Git', '关于页技术栈（逗号分隔）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `blog_site_setting` WHERE `setting_key` = 'about_tech_stack');

INSERT INTO `ums_resource` (`create_time`, `name`, `url`, `description`, `category_id`)
SELECT NOW(), '留言板管理', '/guestbook/**', '留言板管理', rc.id
FROM `ums_resource_category` rc
WHERE rc.name = '博客模块'
  AND NOT EXISTS (SELECT 1 FROM `ums_resource` r WHERE r.url = '/guestbook/**');

INSERT INTO `ums_role_resource_relation` (`role_id`, `resource_id`)
SELECT 5, r.id
FROM `ums_resource` r
WHERE r.url = '/guestbook/**'
  AND NOT EXISTS (
    SELECT 1 FROM `ums_role_resource_relation` rr
    WHERE rr.role_id = 5 AND rr.resource_id = r.id
  );
