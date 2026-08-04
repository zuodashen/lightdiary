#!/usr/bin/env bash
# 验证已有 Redis 容器是否可用
# 用法: ./deploy/verify-redis.sh [redis容器名] [用户名] [密码]

set -e

REDIS_CONTAINER="${1:-redis}"
REDIS_USER="${2:-root}"
REDIS_PASS="${3:-}"

if [ -z "$REDIS_PASS" ]; then
  read -rsp "Redis 密码: " REDIS_PASS
  echo
fi

echo ">>> 检查容器 ${REDIS_CONTAINER}..."
docker ps --format '{{.Names}}' | grep -qx "${REDIS_CONTAINER}" || {
  echo "错误: 找不到容器 ${REDIS_CONTAINER}"
  exit 1
}

echo ">>> 尝试 ACL 登录 (user=${REDIS_USER})..."
if docker exec "${REDIS_CONTAINER}" redis-cli --user "${REDIS_USER}" -a "${REDIS_PASS}" ping 2>/dev/null | grep -qx PONG; then
  echo "OK: Redis ACL 认证成功 (username + password)"
  exit 0
fi

echo ">>> ACL 失败，尝试仅密码认证 (requirepass)..."
if docker exec "${REDIS_CONTAINER}" redis-cli -a "${REDIS_PASS}" ping 2>/dev/null | grep -qx PONG; then
  echo "OK: Redis requirepass 认证成功"
  echo "提示: deploy/.env 中请将 REDIS_USERNAME 留空，仅保留 REDIS_PASSWORD"
  exit 0
fi

echo "失败: 无法连接 Redis，请检查密码或 ACL 配置"
exit 1
