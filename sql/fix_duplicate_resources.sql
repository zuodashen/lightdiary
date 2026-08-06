-- 清理 ums_resource 重复 URL（可重复执行）
-- Navicat：选中全部 → 运行
-- 命令行：mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/fix_duplicate_resources.sql

SET NAMES utf8mb4;

-- ========== 0. 执行前预览（将被删除的重复资源） ==========
SELECT
  r.id          AS drop_id,
  d.keep_id     AS keep_id,
  r.url,
  r.name
FROM ums_resource r
INNER JOIN (
  SELECT url, MIN(id) AS keep_id
  FROM ums_resource
  GROUP BY url
  HAVING COUNT(*) > 1
) d ON d.url = r.url AND r.id <> d.keep_id
ORDER BY r.url, r.id;

-- ========== 1. 合并角色关联到保留的最小 id ==========
INSERT INTO ums_role_resource_relation (role_id, resource_id)
SELECT rr.role_id, d.keep_id
FROM ums_role_resource_relation rr
INNER JOIN ums_resource r ON r.id = rr.resource_id
INNER JOIN (
  SELECT url, MIN(id) AS keep_id
  FROM ums_resource
  GROUP BY url
  HAVING COUNT(*) > 1
) d ON d.url = r.url AND r.id <> d.keep_id
WHERE NOT EXISTS (
  SELECT 1
  FROM ums_role_resource_relation x
  WHERE x.role_id = rr.role_id AND x.resource_id = d.keep_id
);

-- ========== 2. 删除重复资源的角色关联（显式 WHERE，Navicat 安全模式可过） ==========
DELETE FROM ums_role_resource_relation
WHERE resource_id IN (
  SELECT drop_id FROM (
    SELECT r.id AS drop_id
    FROM ums_resource r
    INNER JOIN (
      SELECT url, MIN(id) AS keep_id
      FROM ums_resource
      GROUP BY url
      HAVING COUNT(*) > 1
    ) d ON d.url = r.url AND r.id <> d.keep_id
  ) t
);

-- ========== 3. 删除重复资源本身 ==========
DELETE FROM ums_resource
WHERE id IN (
  SELECT drop_id FROM (
    SELECT r.id AS drop_id
    FROM ums_resource r
    INNER JOIN (
      SELECT url, MIN(id) AS keep_id
      FROM ums_resource
      GROUP BY url
      HAVING COUNT(*) > 1
    ) d ON d.url = r.url AND r.id <> d.keep_id
  ) t
);

-- ========== 4. 验证：应 0 行（无重复 URL） ==========
SELECT url, COUNT(*) AS cnt, GROUP_CONCAT(id ORDER BY id) AS ids
FROM ums_resource
GROUP BY url
HAVING cnt > 1;
