#!/bin/sh
#
# lightdiary 后端一键部署（参考 body-build-project install.sh）
# 用法:
#   1. 上传 lightdiary.jar 和 .env 到本目录
#   2. chmod +x install.sh && ./install.sh
#

set -e

PROJECT_NAME=lightdiary
PROJECT_JAR=lightdiary.jar
SERVER_PORT=8081

CURRENT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "${CURRENT_DIR}"

is_container_running() {
  docker ps -q -f "name=^/${PROJECT_NAME}$" | grep -q .
}

compose_cmd() {
  if command -v docker-compose >/dev/null 2>&1; then
    echo "docker-compose"
  else
    echo "docker compose"
  fi
}

COMPOSE=$(compose_cmd)

echo "========== lightdiary 部署 =========="

# 1. 检查 JAR
if [ ! -f "$PROJECT_JAR" ]; then
  echo "错误: 请先上传 '$PROJECT_JAR' 到当前目录"
  echo "提示: mvn package 后将 target/lightdiary-1.0.0-SNAPSHOT.jar 重命名/upload 为 lightdiary.jar"
  exit 1
fi

# 2. 检查 .env
if [ ! -f ".env" ]; then
  echo "错误: 请先复制 .env.example 为 .env 并填写密码"
  echo "  cp .env.example .env && vi .env"
  exit 1
fi

# 读取端口（若 .env 中配置了 SERVER_PORT）
if grep -q '^SERVER_PORT=' .env 2>/dev/null; then
  SERVER_PORT=$(grep '^SERVER_PORT=' .env | cut -d= -f2 | tr -d '\r')
fi

# 3. 检查 Docker
if ! command -v docker >/dev/null 2>&1; then
  echo "错误: 未安装 Docker"
  exit 1
fi

# 4. 停止旧容器
if is_container_running; then
  docker stop "$PROJECT_NAME" && docker rm "$PROJECT_NAME"
  echo "已停止并移除旧容器 '$PROJECT_NAME'"
else
  echo "未发现运行中的 '$PROJECT_NAME' 容器，继续..."
fi

# 5. 构建并启动
mkdir -p /mydata/lightdiary/logs bak
echo "正在构建并启动..."
# 清除 shell 中可能残留的 bridge IP（会覆盖 .env 中的 127.0.0.1）
unset MYSQL_HOST REDIS_HOST MYSQL_USER MYSQL_PASSWORD REDIS_PASSWORD 2>/dev/null || true
$COMPOSE up --build -d

if [ $? -ne 0 ]; then
  echo "错误: Docker Compose 启动失败"
  exit 1
fi

# 6. 等待启动并验证
echo "等待应用启动..."
sleep 8

HTTP_CODE=$(curl -s -o /tmp/lightdiary_check.json -w "%{http_code}" "http://127.0.0.1:${SERVER_PORT}/api/settings" || true)
if [ "$HTTP_CODE" = "200" ]; then
  echo "✓ API 验证通过 (HTTP 200) http://127.0.0.1:${SERVER_PORT}/api/settings"
  head -c 120 /tmp/lightdiary_check.json 2>/dev/null || true
  echo ""
else
  echo "⚠ API 未返回 200 (当前 HTTP ${HTTP_CODE})，请查看日志:"
  echo "  docker logs --tail 80 ${PROJECT_NAME}"
  rm -f /tmp/lightdiary_check.json
  exit 1
fi
rm -f /tmp/lightdiary_check.json

# 7. 备份 JAR（保留最近 7 个）
TAG=$(date "+%Y%m%d%H%M")
cp "./$PROJECT_JAR" "./bak/$PROJECT_JAR.$TAG"

cd bak
keep=7
count=$(ls -1 "$PROJECT_JAR".* 2>/dev/null | wc -l)
while [ "$count" -gt "$keep" ]; do
  del_file=$(ls -1t "$PROJECT_JAR".* | tail -1)
  rm -f "$del_file"
  echo "删除旧备份: $del_file"
  count=$(ls -1 "$PROJECT_JAR".* 2>/dev/null | wc -l)
done
cd ..

echo ""
echo "========== $PROJECT_NAME 部署完成 =========="
echo "  后端地址: http://127.0.0.1:${SERVER_PORT}"
echo "  查看日志: docker logs -f ${PROJECT_NAME}"
echo "  Nginx 反代请指向: http://172.17.0.1:${SERVER_PORT}"
echo "============================================="
