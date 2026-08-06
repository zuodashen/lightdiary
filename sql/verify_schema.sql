-- lightdiary 表结构验证脚本
-- 用法: docker exec -i mysql mysql -uroot -p light_diary < sql/verify_schema.sql

SELECT '========== 1. 数据库 ==========' AS step;
SELECT DATABASE() AS current_db;

SELECT '========== 2. 表数量（期望 21）==========' AS step;
SELECT COUNT(*) AS table_count
FROM information_schema.tables
WHERE table_schema = 'light_diary';

SELECT '========== 3. 全部表名 ==========' AS step;
SHOW TABLES;

SELECT '========== 4. UMS 权限表（期望 9 张）==========' AS step;
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'light_diary'
  AND table_name LIKE 'ums_%'
ORDER BY table_name;

SELECT '========== 5. 博客业务表（期望 12 张）==========' AS step;
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'light_diary'
  AND table_name LIKE 'blog_%'
ORDER BY table_name;

SELECT '========== 6. 关键初始数据 ==========' AS step;
SELECT 'ums_admin' AS tbl, COUNT(*) AS cnt FROM ums_admin
UNION ALL SELECT 'ums_role', COUNT(*) FROM ums_role
UNION ALL SELECT 'blog_site_setting', COUNT(*) FROM blog_site_setting
UNION ALL SELECT 'blog_nav_item', COUNT(*) FROM blog_nav_item
UNION ALL SELECT 'blog_category', COUNT(*) FROM blog_category
UNION ALL SELECT 'blog_comment_config', COUNT(*) FROM blog_comment_config;

SELECT '========== 7. 管理员账号（应有 test / admin）==========' AS step;
SELECT id, username, status, nick_name FROM ums_admin;

SELECT '========== 8. 缺失表检查 ==========' AS step;
SELECT expected.table_name AS missing_table
FROM (
  SELECT 'ums_admin' AS table_name UNION ALL
  SELECT 'ums_admin_login_log' UNION ALL
  SELECT 'ums_admin_role_relation' UNION ALL
  SELECT 'ums_menu' UNION ALL
  SELECT 'ums_resource' UNION ALL
  SELECT 'ums_resource_category' UNION ALL
  SELECT 'ums_role' UNION ALL
  SELECT 'ums_role_menu_relation' UNION ALL
  SELECT 'ums_role_resource_relation' UNION ALL
  SELECT 'blog_article' UNION ALL
  SELECT 'blog_category' UNION ALL
  SELECT 'blog_tag' UNION ALL
  SELECT 'blog_article_tag' UNION ALL
  SELECT 'blog_page' UNION ALL
  SELECT 'blog_bookmark_category' UNION ALL
  SELECT 'blog_bookmark' UNION ALL
  SELECT 'blog_nav_item' UNION ALL
  SELECT 'blog_social_link' UNION ALL
  SELECT 'blog_site_setting' UNION ALL
  SELECT 'blog_comment' UNION ALL
  SELECT 'blog_comment_config' UNION ALL
  SELECT 'blog_innovation'
) expected
LEFT JOIN information_schema.tables t
  ON t.table_schema = 'light_diary' AND t.table_name = expected.table_name
WHERE t.table_name IS NULL;
