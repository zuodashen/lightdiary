-- 微光实验室模块（创新项目展示）
-- 已有库执行：mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/migrate_innovation.sql

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `blog_innovation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '项目名称',
  `slug` varchar(200) DEFAULT NULL COMMENT 'URL标识',
  `summary` varchar(500) DEFAULT NULL COMMENT '一句话介绍',
  `description` text COMMENT '详细描述',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图',
  `demo_url` varchar(500) DEFAULT NULL COMMENT '在线演示地址',
  `github_url` varchar(500) DEFAULT NULL COMMENT '源码地址',
  `tech_stack` varchar(500) DEFAULT NULL COMMENT '技术栈，逗号分隔',
  `status` varchar(20) NOT NULL DEFAULT 'BUILDING' COMMENT 'IDEA构思/BUILDING进行中/LIVE已上线',
  `is_featured` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否精选展示',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_innovation_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微光实验室-创新项目';

INSERT INTO `blog_innovation` (`title`, `slug`, `summary`, `description`, `demo_url`, `github_url`, `tech_stack`, `status`, `is_featured`, `sort_order`, `create_time`, `update_time`)
SELECT '股票分析小工具', 'stock-agent', '基于 Agent 的智能股票分析助手', '探索 AI Agent 在金融分析场景下的应用，自动化收集信息并生成分析报告。', NULL, 'https://github.com/zuodashen/lightdiary', 'Python,Agent,LLM', 'LIVE', 1, 1, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM blog_innovation WHERE slug = 'stock-agent');

INSERT INTO `blog_nav_item` (`name`, `path`, `icon`, `parent_id`, `sort_order`, `is_external`)
SELECT '实验室', '/lab', 'fa-solid fa-flask', 0, 6, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM blog_nav_item WHERE path = '/lab');

UPDATE blog_nav_item SET sort_order = 7 WHERE path = '/about';

INSERT INTO `ums_resource` (`create_time`, `name`, `url`, `description`, `category_id`)
SELECT NOW(), '实验室管理', '/innovation/**', '微光实验室创新项目管理', rc.id
FROM ums_resource_category rc
WHERE rc.name = '博客模块'
  AND NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/innovation/**')
LIMIT 1;

INSERT INTO `ums_role_resource_relation` (`role_id`, `resource_id`)
SELECT 5, id FROM ums_resource WHERE url = '/innovation/**'
AND NOT EXISTS (
  SELECT 1 FROM ums_role_resource_relation r
  INNER JOIN ums_resource res ON res.id = r.resource_id
  WHERE r.role_id = 5 AND res.url = '/innovation/**'
);
