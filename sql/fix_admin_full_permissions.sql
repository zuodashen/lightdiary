-- 修复 admin 无法访问「角色管理 / 实验室管理」等后台接口
-- 典型原因：只执行了 light_diary_blog.sql，超级管理员(role_id=5)仅有博客模块权限，缺少 /role/** 等权限模块资源
--
-- mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/fix_admin_full_permissions.sql

SET NAMES utf8mb4;

-- ========== 1. 确保权限模块资源存在 ==========
INSERT INTO ums_resource_category (create_time, name, sort)
SELECT NOW(), '权限模块', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ums_resource_category WHERE name = '权限模块');

SET @perm_cat_id = (SELECT id FROM ums_resource_category WHERE name = '权限模块' LIMIT 1);

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '后台用户管理', '/admin/**', '后台用户管理', @perm_cat_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/admin/**');

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '后台用户角色管理', '/role/**', '后台用户角色管理', @perm_cat_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/role/**');

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '后台菜单管理', '/menu/**', '后台菜单管理', @perm_cat_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/menu/**');

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '后台资源分类管理', '/resourceCategory/**', '后台资源分类管理', @perm_cat_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/resourceCategory/**');

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), '后台资源管理', '/resource/**', '后台资源管理', @perm_cat_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ums_resource WHERE url = '/resource/**');

-- ========== 2. 确保博客模块资源存在（含实验室） ==========
INSERT INTO ums_resource_category (create_time, name, sort)
SELECT NOW(), '博客模块', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ums_resource_category WHERE name = '博客模块');

SET @blog_cat_id = (SELECT id FROM ums_resource_category WHERE name = '博客模块' LIMIT 1);

INSERT INTO ums_resource (create_time, name, url, description, category_id)
SELECT NOW(), t.name, t.url, t.description, @blog_cat_id
FROM (
  SELECT '文章管理' AS name, '/article/**' AS url, '博客文章管理' AS description UNION ALL
  SELECT '分类管理', '/category/**', '博客分类管理' UNION ALL
  SELECT '标签管理', '/tag/**', '博客标签管理' UNION ALL
  SELECT '页面管理', '/page/**', '博客页面管理' UNION ALL
  SELECT '书签分类管理', '/bookmarkCategory/**', '书签分类管理' UNION ALL
  SELECT '书签管理', '/bookmark/**', '书签管理' UNION ALL
  SELECT '实验室管理', '/innovation/**', '微光实验室创新项目管理' UNION ALL
  SELECT '导航管理', '/navItem/**', '导航管理' UNION ALL
  SELECT '社交链接管理', '/socialLink/**', '社交链接管理' UNION ALL
  SELECT '站点设置管理', '/siteSetting/**', '站点设置管理' UNION ALL
  SELECT '评论管理', '/comment/**', '评论管理'
) t
WHERE NOT EXISTS (SELECT 1 FROM ums_resource r WHERE r.url = t.url);

-- ========== 3. 超级管理员拥有全部资源 ==========
INSERT INTO ums_role_resource_relation (role_id, resource_id)
SELECT 5, r.id
FROM ums_resource r
WHERE NOT EXISTS (
  SELECT 1 FROM ums_role_resource_relation rr
  WHERE rr.role_id = 5 AND rr.resource_id = r.id
);

-- ========== 4. admin 用户绑定超级管理员角色 ==========
INSERT INTO ums_admin_role_relation (admin_id, role_id)
SELECT a.id, 5
FROM ums_admin a
WHERE a.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM ums_admin_role_relation ar
    WHERE ar.admin_id = a.id AND ar.role_id = 5
  );

-- ========== 5. 验证 ==========
SELECT 'admin_user' AS check_item, a.id, a.username, a.status
FROM ums_admin a WHERE a.username = 'admin';

SELECT 'admin_roles' AS check_item, ar.admin_id, ar.role_id, r.name
FROM ums_admin_role_relation ar
JOIN ums_role r ON r.id = ar.role_id
JOIN ums_admin a ON a.id = ar.admin_id
WHERE a.username = 'admin';

SELECT 'role5_resource_count' AS check_item, COUNT(*) AS cnt
FROM ums_role_resource_relation WHERE role_id = 5;

SELECT 'admin_has_role_perm' AS check_item, ur.url
FROM ums_admin a
JOIN ums_admin_role_relation ar ON ar.admin_id = a.id
JOIN ums_role_resource_relation rrr ON rrr.role_id = ar.role_id
JOIN ums_resource ur ON ur.id = rrr.resource_id
WHERE a.username = 'admin' AND ur.url IN ('/role/**', '/innovation/**', '/admin/**');
