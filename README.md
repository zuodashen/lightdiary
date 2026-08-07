# lightdiary · 微光博客

基于 Spring Boot + Vue 3 的前后端分离个人博客，品牌名 **lightdiary / 微光博客**。

| 模块 | 技术 | 说明 |
| ---- | ---- | ---- |
| 后端 | Spring Boot 2.7.5 + MyBatis-Plus | 博客 API + 权限管理 |
| 前台 | Vue 3 + Vite + Tailwind (`blog-web`) | 暗色主题博客站点 |
| 后台 | Vue 3 + Vite + Tailwind (`blog-admin`) | 可视化内容管理 + 登录 |
| 数据 | MySQL + Redis | 文章、书签、站点配置等 |

> 后端脚手架学习自 [mall-tiny](https://github.com/macrozheng/mall-tiny)
> 前端页面学习自 https://github.com/jianyuewushuang/jianyueblog
## 快速开始

### 1. 环境

- JDK 8+、Maven、Node.js 18+
- MySQL、Redis

### 2. 数据库

**全新部署只需按顺序执行以下两个 SQL**（不要跳过 `light_diary.sql`，也不要额外跑 `fix_*` / `migrate_*`）：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS light_diary DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/light_diary.sql
mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/light_diary_blog.sql
```

| 文件 | 内容 |
| ---- | ---- |
| `sql/light_diary.sql` | 权限系统（用户/角色/菜单/资源，含 `/role/**`、`/admin/**` 等） |
| `sql/light_diary_blog.sql` | 博客业务表 + 博客权限 + 实验室 + 导航种子数据 |

已有旧库升级时才使用 `sql/migrate_*.sql`；权限异常时才用 `sql/fix_*.sql` 补救。

本地密码不要写进 Git。复制示例后填写：

```bash
cp src/main/resources/application-dev-local.yml.example \
   src/main/resources/application-dev-local.yml
# 编辑 application-dev-local.yml 填入 MySQL / Redis 密码
```

### 3. 启动后端

```bash
mvn clean spring-boot:run
```

启动成功后访问：

- 接口文档：http://127.0.0.1:8080/doc.html
- 前台 API：http://127.0.0.1:8080/api/settings

### 4. 启动前端

```bash
cd blog-web
npm install
npm run dev
```

访问：http://localhost:5173

### 5. 启动管理后台

```bash
cd blog-admin
npm install
npm run dev
```

访问：http://localhost:5174

使用 `ums_admin` 表中的账号登录（常见为 `test/test` 或 `admin/macro123`）。管理后台覆盖全部 Admin API：

| 模块 | 功能 |
| ---- | ---- |
| 内容 | 文章、分类、标签、书签、页面、评论审核与配置 |
| 站点 | 站点设置、导航、社交链接 |
| 系统 | 用户、角色（菜单/资源分配）、菜单、资源 |
| 账户 | 修改密码、退出登录 |

## 生产部署

部署脚本与文档在本地 `deploy/` 目录（**不提交 Git**，含服务器路径与 `.env` 模板）。

本地开发打包示例：

```bash
mvn clean package -DskipTests
# 将 JAR 上传到服务器后执行 install.sh，详见本地 deploy/DEPLOY-FULL.md
```

## 项目结构

```
lightdiary/
├── sql/                    # 数据库脚本
├── src/                    # Spring Boot 后端
│   └── modules/
│       ├── ums/            # 权限管理
│       └── blog/           # 博客业务
├── blog-web/               # Vue 3 前台站点
├── blog-admin/             # Vue 3 管理后台
└── README.md
```

## 内容管理

初始数据库仅包含站点配置、导航、分类框架，**文章和书签需自行添加**。

**推荐方式：** 使用管理后台 http://localhost:5174 进行可视化操作。

**备选方式（Swagger）：**

1. 打开 http://127.0.0.1:8080/doc.html
2. 调用 `POST /admin/login` 登录（默认账号见 `ums_admin` 表，常见为 `test/test` 或 `admin/macro123`）
3. 点击 **Authorize**，填入 `Bearer {token}`
4. 通过以下接口管理内容：

| 接口前缀 | 功能 |
| -------- | ---- |
| `/article/**` | 文章 |
| `/category/**`、`/tag/**` | 分类、标签 |
| `/bookmarkCategory/**`、`/bookmark/**` | 书签 |
| `/page/**` | 自定义页面（如关于页） |
| `/siteSetting/**` | 站点设置 |

## 前台 API

无需认证，前缀 `/api`：

- `GET /api/articles` — 文章列表
- `GET /api/articles/{slug}` — 文章详情
- `GET /api/bookmarks` — 书签
- `GET /api/settings` — 站点配置

## 品牌更新（已有旧数据时）

```bash
mysql -u root -p light_diary < sql/update_site_branding.sql
redis-cli DEL blog:settings blog:social
```

---

以下为脚手架原始开发文档（权限模块、代码生成器等）。
