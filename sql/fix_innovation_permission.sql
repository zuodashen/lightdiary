-- 修复 admin 无法访问「实验室管理」权限问题
-- mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/fix_innovation_permission.sql
--
-- 原因：/innovation/** 资源未写入，或未分配给超级管理员角色(role_id=5)，或 Redis 缓存了旧权限

SET NAMES utf8mb4;

-- 1. 确保资源存在
INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '实验室管理', '/innovation/**', '微光实验室创新项目管理', rc.id
FROM ums_resource_category rc
WHERE rc.name = '博客模块'
  AND NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/innovation/**')
LIMIT 1;

-- 若上面因分类名不匹配未插入，则用任意博客资源所属分类
INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '实验室管理', '/innovation/**', '微光实验室创新项目管理', MIN(category_id)
FROM ums_resource
WHERE url LIKE '/article/%'
  AND NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/innovation/**');

-- 2. 分配给超级管理员角色 (role_id = 5)
INSERT INTO ums_role_resource_relation (role_id, resource_id)
SELECT 5, r.id
FROM ums_resource r
WHERE r.url = '/innovation/**'
  AND NOT EXISTS (
    SELECT 1 FROM ums_role_resource_relation rr
    WHERE rr.role_id = 5 AND rr.resource_id = r.id
  );

-- 3. 验证（应各返回 1 行）
SELECT 'resource' AS item, id, name, url FROM ums_resource WHERE url = '/innovation/**';
SELECT 'role_bind' AS item, rr.role_id, rr.resource_id
FROM ums_role_resource_relation rr
JOIN ums_resource r ON r.id = rr.resource_id
WHERE r.url = '/innovation/**' AND rr.role_id = 5;
SELECT 'admin_role' AS item, ar.admin_id, a.username, ar.role_id
FROM ums_admin_role_relation ar
JOIN ums_admin a ON a.id = ar.admin_id
WHERE a.username = 'admin';
