# lightdiary 阿里云部署指南

> 适配你的服务器环境：nginx(bridge:80/8080) + mysql8(host) + redis(host)  
> 数据库对外域名：**db01.hzlinks.com** → 8.155.149.208（本机部署仍用 127.0.0.1）

---

## 环境对照表：localhost vs 服务器

| 配置项 | 本地开发 (application-dev.yml) | 服务器部署 (deploy/server/.env) | 说明 |
|--------|-------------------------------|----------------------------------|------|
| **MySQL 地址** | `localhost` 或 **`db01.hzlinks.com`** | **`127.0.0.1`** | 后端与 mysql8 同机 → 走本机回环，不要用域名 |
| MySQL 端口 | `3306` | `3306` | 相同 |
| MySQL 用户/密码 | root / 你的密码 | root / 你的密码 | 相同 |
| **Redis 地址** | `localhost` | **`127.0.0.1`** | redis 也是 host 网络，同机用回环 |
| Redis 用户/密码 | 本地可能为空 | root / 你的密码 | 按实际 redis 配置 |
| **后端 API 端口** | `8080` | **`8081`** | 服务器 8080 已被 nginx 占用 |
| 前台 dev 地址 | `http://localhost:5173` | nginx 80 域名 | Vite 开发服务器 |
| 后台 dev 地址 | `http://localhost:5174` | nginx admin 域名 | 管理后台 |
| **站点 URL (DB)** | `http://localhost:5173` | **`http://你的博客域名`** | `blog_site_setting.site_url` |
| Nginx 反代后端 | Vite proxy → localhost:8080 | **`172.17.0.1:8081`** | bridge 容器访问 host 上的后端 |

### db01.hzlinks.com 什么时候用？

```
┌─────────────────┐         db01.hzlinks.com:3306          ┌──────────────────┐
│  你的 Mac 本地   │  ──────────────────────────────────► │  8.155.149.208   │
│  mvn / IDEA 开发 │                                        │  mysql8 (host)   │
└─────────────────┘                                        └──────────────────┘

┌─────────────────┐         127.0.0.1:3306                   ┌─────────────────┐
│ lightdiary 容器 │  ──────────────────────────────────► │ mysql8 同机      │
│ (host 网络)     │         （不走公网域名）                 └─────────────────┘
└─────────────────┘
```

- **本地电脑连远程库开发** → `db01.hzlinks.com`（或 `8.155.149.208`）
- **服务器上跑 lightdiary** → `127.0.0.1`（**不要**写 db01.hzlinks.com）

---

## 架构一览

```
用户浏览器
    │
    ▼
nginx :80  ──静态──► blog-web / blog-admin (dist)
    │
    └──反代──► 172.17.0.1:8081  (lightdiary 后端, host 网络)
                    │
                    ├── 127.0.0.1:3306  mysql8
                    └── 127.0.0.1:6379  redis
```

| 组件 | 端口 | 说明 |
|------|------|------|
| nginx | 80, 8080 | 已有，8080 不要给 lightdiary 用 |
| lightdiary | **8081** | host 网络，与 mysql8/redis 一致 |
| mysql8 | 3306 | host 网络 |
| redis | 6379 | host 网络 |

---

## 一、首次部署（只做一次）

### 1. 初始化数据库

```bash
# MySQL 容器名是 mysql8
docker exec -i mysql8 mysql -uroot -p'你的密码' -e \
  "CREATE DATABASE IF NOT EXISTS light_diary DEFAULT CHARSET utf8mb4;"

docker exec -i mysql8 mysql -uroot -p'你的密码' light_diary < sql/light_diary.sql
docker exec -i mysql8 mysql -uroot -p'你的密码' light_diary < sql/light_diary_blog.sql

# 验证（应 21 张表）
docker exec -i mysql8 mysql -uroot -p'你的密码' light_diary < sql/verify_schema.sql
```

### 2. 验证 Redis

```bash
docker exec redis redis-cli --user root -a '你的密码' ping
# 期望 PONG
```

### 3. 部署后端（一键）

**本地打包：**

```bash
mvn clean package -DskipTests
```

**上传到服务器 `/opt/lightdiary/server/`：**

```bash
# 本地执行
scp target/lightdiary-1.0.0-SNAPSHOT.jar root@8.155.149.208:/opt/lightdiary/server/lightdiary.jar
scp -r deploy/server/* root@8.155.149.208:/opt/lightdiary/server/
scp -r sql root@8.155.149.208:/opt/lightdiary/
```

**服务器配置并启动：**

```bash
cd /opt/lightdiary/server

cp .env.example .env
vi .env          # 填入 MySQL/Redis 密码

chmod +x install.sh
./install.sh
```

成功输出应包含：`✓ API 验证通过 (HTTP 200)`

手动验证：

```bash
curl -s http://127.0.0.1:8081/api/settings | head -c 200
# 期望 {"code":200,...}  不是 401
```

> **注意**：`curl 8080` 打到的是 nginx，不是 lightdiary，会返回 401，属正常。

### 4. 部署前端

**本地构建：**

```bash
cd blog-web && npm ci && npm run build
cd ../blog-admin && npm ci && npm run build
```

**上传到 nginx 静态目录：**

```bash
scp -r blog-web/dist/* root@服务器:/mydata/nginx/html/lightdiary-web/
scp -r blog-admin/dist/* root@服务器:/mydata/nginx/html/lightdiary-admin/
```

### 5. 配置 Nginx

将 `deploy/nginx/lightdiary.conf` 复制到 nginx 配置目录，修改 `server_name`，确认反代地址为 **8081**：

```nginx
proxy_pass http://172.17.0.1:8081/api/;      # 前台
proxy_pass http://172.17.0.1:8081;           # 后台 Admin API
```

```bash
docker exec nginx nginx -t
docker exec nginx nginx -s reload
```

### 6. 更新站点 URL + 清缓存

```bash
docker exec mysql8 mysql -uroot -p'密码' light_diary -e \
  "UPDATE blog_site_setting SET setting_value='http://你的域名' WHERE setting_key='site_url';"

docker exec redis redis-cli --user root -a '密码' DEL blog:settings blog:social blog:nav
```

### 7. 访问验证

| 地址 | 说明 |
|------|------|
| `http://你的域名` | 前台博客 |
| `http://admin.你的域名` | 后台，账号 test/test |

---

## 二、日常更新（只改后端）

与 body-build-project 相同流程：

```bash
# 1. 本地打包
mvn clean package -DskipTests

# 2. 上传 JAR（覆盖 server 目录下的 lightdiary.jar）
scp target/lightdiary-1.0.0-SNAPSHOT.jar root@服务器:/opt/lightdiary/server/lightdiary.jar

# 3. 服务器一键重启
cd /opt/lightdiary/server && ./install.sh
```

旧 JAR 自动备份到 `server/bak/`，保留最近 7 个。

---

## 三、日常更新（只改前端）

```bash
cd blog-web && npm run build
scp -r dist/* root@服务器:/mydata/nginx/html/lightdiary-web/

cd blog-admin && npm run build
scp -r dist/* root@服务器:/mydata/nginx/html/lightdiary-admin/
```

无需重启后端。

---

## 四、目录结构（服务器）

```
/opt/lightdiary/
├── sql/                          # 数据库脚本
│   ├── light_diary.sql
│   ├── light_diary_blog.sql
│   └── verify_schema.sql
└── server/                       # 后端一键部署包（与 body-build 同结构）
    ├── Dockerfile
    ├── docker-compose.yml
    ├── install.sh
    ├── .env                      # 密码配置（不上传 Git）
    ├── .env.example
    ├── lightdiary.jar            # 上传的 JAR
    └── bak/                      # 历史 JAR 备份

/mydata/nginx/html/
├── lightdiary-web/               # 前台 dist
└── lightdiary-admin/             # 后台 dist

/mydata/lightdiary/logs/          # 后端日志
```

---

## 五、deploy/server/.env 模板

```env
SERVER_PORT=8081

MYSQL_HOST=127.0.0.1
MYSQL_PORT=13306
MYSQL_USER=root
MYSQL_PASSWORD=Hzsun@310012

REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_USERNAME=root
REDIS_PASSWORD=Hzsun@310012
REDIS_DATABASE=0

JWT_SECRET=请改为随机长字符串
KNIFE4J_ENABLE=false
```

---

## 六、常用命令

```bash
docker logs -f lightdiary              # 看日志
docker restart lightdiary              # 重启
curl http://127.0.0.1:8081/api/settings  # 测 API
docker exec redis redis-cli --user root -a '密码' DEL blog:settings  # 清站点缓存
```

---

## 七、故障排查

| 现象 | 原因 | 处理 |
|------|------|------|
| `127.0.0.1:3306` Communications link failure，`ss` 显示 `*:13306` 无 `3306` | 宿主机 MySQL 监听 **13306** 而非 3306 | `.env` 加 `MYSQL_PORT=13306` 重建；**旧 JAR 未支持 MYSQL_PORT 时**见下方临时方案 |
| `docker exec lightdiary env` 里 MYSQL_HOST 是 `172.18.x.x` 但 `.env` 是 `127.0.0.1` | shell 里 `export MYSQL_HOST=容器IP` 被 compose 的 `${MYSQL_HOST}` 覆盖 `.env` | `unset MYSQL_HOST REDIS_HOST && ./install.sh`；或更新 `docker-compose.yml` 后重建 |
| `jdbc:mysql://172.18.x.x` 或 `172.17.x.x` **connect timed out** | `.env` 里写了容器 bridge IP；mysql8 是 **host 网络**，不在 bridge 上 | 改 `MYSQL_HOST=127.0.0.1`，`docker compose up -d --force-recreate` |
| `8080` 返回 401 | 打到 nginx 了 | 改用 **8081** 测 lightdiary |
| install.sh API 非 200 | MySQL/Redis 连不上 | `docker logs lightdiary` 查错 |
| 前台网络错误 | nginx 未反代到 8081 | 检查 `172.17.0.1:8081` |
| Docker 拉镜像超时 | docker.io 不通 | 已用 `wxws/openjdk:8-jre` 阿里云源 |
| 表不是 21 张 | SQL 未导入 | 重新导入两个 sql 文件 |

**旧 JAR 临时改 MySQL 端口（无需重新打包）：** 在 `.env` 增加一行（Spring Boot 会覆盖 yml）：

```env
SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:13306/light_diary?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
```

然后 `docker compose up -d --force-recreate`。

---

## 八、与 body-build-project 的对应关系

| body-build | lightdiary |
|------------|------------|
| `body-build-project.jar` | `lightdiary.jar` |
| `install.sh` | `deploy/server/install.sh` |
| `Dockerfile` (wxws/openjdk) | `deploy/server/Dockerfile` |
| `docker-compose up --build -d` | 相同 |
| `bak/` 保留 7 份 | 相同 |
