-- ============================================
-- lightdiary 博客业务表
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 文章表
DROP TABLE IF EXISTS `blog_article`;
CREATE TABLE `blog_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `slug` varchar(255) DEFAULT NULL COMMENT 'URL友好标识',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext NOT NULL COMMENT 'Markdown原始内容',
  `content_html` longtext COMMENT '渲染后的HTML（缓存）',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT草稿/PUBLISHED已发布',
  `views` int(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `is_top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `allow_comment` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许评论',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 分类表
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `slug` varchar(100) DEFAULT NULL COMMENT 'URL友好标识',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父分类ID（0为顶级）',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(100) DEFAULT NULL COMMENT 'FontAwesome图标类名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 标签表
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '标签名称',
  `slug` varchar(100) DEFAULT NULL COMMENT 'URL友好标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

-- 文章-标签关联表
DROP TABLE IF EXISTS `blog_article_tag`;
CREATE TABLE `blog_article_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联表';

-- 自定义页面表
DROP TABLE IF EXISTS `blog_page`;
CREATE TABLE `blog_page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '页面标题',
  `slug` varchar(255) NOT NULL COMMENT 'URL路径标识',
  `content` longtext COMMENT 'Markdown内容',
  `content_html` longtext COMMENT '渲染后的HTML',
  `template` varchar(100) DEFAULT NULL COMMENT '自定义模板名',
  `status` varchar(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义页面表';

-- 书签分类表
DROP TABLE IF EXISTS `blog_bookmark_category`;
CREATE TABLE `blog_bookmark_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `icon` varchar(100) DEFAULT NULL COMMENT 'FontAwesome图标类名',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书签分类表';

-- 书签表
DROP TABLE IF EXISTS `blog_bookmark`;
CREATE TABLE `blog_bookmark` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(255) NOT NULL COMMENT '书签名称',
  `link` varchar(500) NOT NULL COMMENT '书签URL',
  `description` varchar(500) DEFAULT NULL COMMENT '书签描述',
  `image` varchar(500) DEFAULT NULL COMMENT '书签图标URL',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书签表';

-- 导航菜单项表
DROP TABLE IF EXISTS `blog_nav_item`;
CREATE TABLE `blog_nav_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '导航项名称',
  `path` varchar(255) DEFAULT NULL COMMENT '路径或URL',
  `icon` varchar(100) DEFAULT NULL COMMENT 'FontAwesome图标',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父菜单ID（0为顶级）',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `is_external` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否外部链接',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航菜单项表';

-- 社交链接表
DROP TABLE IF EXISTS `blog_social_link`;
CREATE TABLE `blog_social_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platform` varchar(50) NOT NULL COMMENT '平台名称',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `url` varchar(500) NOT NULL COMMENT '链接URL',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交链接表';

-- 站点设置表
DROP TABLE IF EXISTS `blog_site_setting`;
CREATE TABLE `blog_site_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(100) NOT NULL COMMENT '配置键名',
  `setting_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点设置表';

-- 评论表
DROP TABLE IF EXISTS `blog_comment`;
CREATE TABLE `blog_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL COMMENT '关联文章ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父评论ID（0为顶级评论）',
  `author_name` varchar(100) NOT NULL COMMENT '评论者名称',
  `author_email` varchar(255) DEFAULT NULL COMMENT '评论者邮箱',
  `author_avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `content` text NOT NULL COMMENT '评论内容（Markdown）',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待审核/APPROVED已通过/SPAM垃圾',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '浏览器UA',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 评论系统配置表
DROP TABLE IF EXISTS `blog_comment_config`;
CREATE TABLE `blog_comment_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system` varchar(50) NOT NULL COMMENT '评论系统名：giscus/built-in',
  `config_json` text COMMENT '评论系统配置JSON',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论系统配置表';

-- 初始数据
INSERT INTO `blog_category` (`name`, `slug`, `description`, `parent_id`, `sort_order`, `create_time`)
VALUES ('技术文档', 'tech', '技术文档分类', 0, 1, NOW());

INSERT INTO `blog_site_setting` (`setting_key`, `setting_value`, `description`) VALUES
('site_title', 'lightdiary', '站点标题'),
('site_subtitle', '微光博客', '站点副标题'),
('site_description', '一个分享知识与技术的微光博客', '站点描述'),
('site_author', 'lightdiary', '站点作者'),
('site_url', 'http://localhost:5173', '站点URL'),
('site_favicon', '/images/avatar.jpg', '站点图标'),
('site_logo', '/images/avatar.jpg', '站点Logo'),
('site_avatar', '/images/avatar.jpg', '站点头像'),
('color_primary', '#7509B6', '主色调'),
('color_secondary', '#0153E5', '副色调'),
('default_theme_mode', 'dark', '默认主题模式'),
('per_page', '10', '每页文章数'),
('sidebar_announcement', '发扬开源精神，共享幸福人生', '侧边栏公告'),
('site_start_time', '2026-07-05 10:10:10', '建站时间'),
('banner_title', '微光博客', '首页Banner标题'),
('banner_subtitle', '一个分享知识与技术的微光博客', '首页Banner副标题'),
('banner_image', '/images/bg.avif', '首页Banner图片');

INSERT INTO `blog_nav_item` (`name`, `path`, `icon`, `parent_id`, `sort_order`, `is_external`) VALUES
('首页', '/', 'fa-solid fa-house', 0, 1, 0),
('归档', '/archives', 'fa-solid fa-archive', 0, 2, 0),
('分类', '/categories', 'fa-solid fa-folder', 0, 3, 0),
('标签', '/tags', 'fa-solid fa-tags', 0, 4, 0),
('书签', '/bookmarks', 'fa-solid fa-bookmark', 0, 5, 0),
('关于', '/about', 'fa-solid fa-user', 0, 6, 0);

INSERT INTO `blog_social_link` (`platform`, `icon`, `url`, `sort_order`) VALUES
('GitHub', 'github', 'https://github.com/zuodashen/lightdiary', 1),
('Email', 'email', 'mailto:1904465424@qq.com', 2);

-- 评论默认关闭，可在后台 /comment/config 或 doc.html 中配置 Giscus
INSERT INTO `blog_comment_config` (`system`, `config_json`, `enabled`) VALUES
('giscus', '{}', 0);

-- 博客管理权限资源
INSERT INTO `ums_resource_category` (`create_time`, `name`, `sort`) VALUES (NOW(), '博客模块', 0);
SET @blog_category_id = LAST_INSERT_ID();

INSERT INTO `ums_resource` (`create_time`, `name`, `url`, `description`, `category_id`) VALUES
(NOW(), '文章管理', '/article/**', '博客文章管理', @blog_category_id),
(NOW(), '分类管理', '/category/**', '博客分类管理', @blog_category_id),
(NOW(), '标签管理', '/tag/**', '博客标签管理', @blog_category_id),
(NOW(), '页面管理', '/page/**', '博客页面管理', @blog_category_id),
(NOW(), '书签分类管理', '/bookmarkCategory/**', '书签分类管理', @blog_category_id),
(NOW(), '书签管理', '/bookmark/**', '书签管理', @blog_category_id),
(NOW(), '导航管理', '/navItem/**', '导航管理', @blog_category_id),
(NOW(), '社交链接管理', '/socialLink/**', '社交链接管理', @blog_category_id),
(NOW(), '站点设置管理', '/siteSetting/**', '站点设置管理', @blog_category_id),
(NOW(), '评论管理', '/comment/**', '评论管理', @blog_category_id);

-- 为超级管理员角色分配博客权限
INSERT INTO `ums_role_resource_relation` (`role_id`, `resource_id`)
SELECT 5, id FROM `ums_resource` WHERE `category_id` = @blog_category_id;

SET FOREIGN_KEY_CHECKS = 1;
