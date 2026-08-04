#!/usr/bin/env bash
# 在服务器上验证 light_diary 库表结构
# 用法: ./deploy/verify-db.sh [mysql容器名] [root密码]

set -e

MYSQL_CONTAINER="${1:-mysql}"
MYSQL_PASSWORD="${2:-}"

if [ -z "$MYSQL_PASSWORD" ]; then
  read -rsp "MySQL root 密码: " MYSQL_PASSWORD
  echo
fi

SCRIPT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

echo ">>> 检查容器 ${MYSQL_CONTAINER} 是否存在..."
docker ps --format '{{.Names}}' | grep -qx "${MYSQL_CONTAINER}" || {
  echo "错误: 找不到容器 ${MYSQL_CONTAINER}"
  echo "可用容器: $(docker ps --format '{{.Names}}' | tr '\n' ' ')"
  exit 1
}

echo ">>> 检查数据库 light_diary..."
docker exec "${MYSQL_CONTAINER}" mysql -uroot -p"${MYSQL_PASSWORD}" -e "SHOW DATABASES LIKE 'light_diary';"

echo ">>> 运行 verify_schema.sql..."
docker exec -i "${MYSQL_CONTAINER}" mysql -uroot -p"${MYSQL_PASSWORD}" light_diary \
  < "${SCRIPT_DIR}/sql/verify_schema.sql"

echo ""
echo ">>> 验证完成。请确认:"
echo "    - table_count = 21"
echo "    - missing_table 结果为空"
echo "    - ums_admin 至少有 1 条记录"
