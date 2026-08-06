-- ============================================
-- 修复 ums 表中文昵称/菜单等乱码（UTF-8 被当作 latin1 导入导致）
-- 使用方式：mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/fix_ums_charset.sql
-- ============================================

SET NAMES utf8mb4;

-- 将误存为 latin1 的 UTF-8 字节还原为正确中文
UPDATE ums_admin
SET nick_name = CONVERT(BINARY CONVERT(nick_name USING latin1) USING utf8mb4),
    note      = CONVERT(BINARY CONVERT(note USING latin1) USING utf8mb4)
WHERE nick_name IS NOT NULL
  AND nick_name REGEXP '[ÃÂæçèéêë]';

UPDATE ums_menu
SET title = CONVERT(BINARY CONVERT(title USING latin1) USING utf8mb4)
WHERE title REGEXP '[ÃÂæçèéêë]';

UPDATE ums_role
SET name        = CONVERT(BINARY CONVERT(name USING latin1) USING utf8mb4),
    description = CONVERT(BINARY CONVERT(description USING latin1) USING utf8mb4)
WHERE name REGEXP '[ÃÂæçèéêë]';

UPDATE ums_resource
SET name        = CONVERT(BINARY CONVERT(name USING latin1) USING utf8mb4),
    description = CONVERT(BINARY CONVERT(description USING latin1) USING utf8mb4)
WHERE name REGEXP '[ÃÂæçèéêë]';

UPDATE ums_resource_category
SET name = CONVERT(BINARY CONVERT(name USING latin1) USING utf8mb4)
WHERE name REGEXP '[ÃÂæçèéêë]';

-- 统一字符集，防止后续再次出现乱码
ALTER TABLE ums_admin CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ums_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ums_role CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ums_resource CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ums_resource_category CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
