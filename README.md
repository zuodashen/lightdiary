# lightdiary · 微光博客

基于 Spring Boot + Vue 3 的前后端分离个人博客，品牌名 **lightdiary / 微光博客**。

| 模块 | 技术 | 说明 |
| ---- | ---- | ---- |
| 后端 | Spring Boot 2.7.5 + MyBatis-Plus | 博客 API + 权限管理 |
| 前端 | Vue 3 + Vite + Tailwind | 暗色主题博客站点 |
| 数据 | MySQL + Redis | 文章、书签、站点配置等 |

> 后端脚手架学习自 [mall-tiny](https://github.com/macrozheng/mall-tiny)
> 前端页面学习自 https://github.com/jianyuewushuang/jianyueblog
## 快速开始

### 1. 环境

- JDK 8+、Maven、Node.js 18+
- MySQL、Redis

### 2. 数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS light_diary DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p light_diary < sql/light_diary.sql
mysql -u root -p light_diary < sql/light_diary_blog.sql
```

修改 `src/main/resources/application-dev.yml` 中的 MySQL / Redis 连接信息。

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

## 项目结构

```
lightdiary/
├── sql/                    # 数据库脚本
├── src/                    # Spring Boot 后端
│   └── modules/
│       ├── ums/            # 权限管理
│       └── blog/           # 博客业务
├── blog-web/               # Vue 3 前端
└── README.md
```

## 内容管理

初始数据库仅包含站点配置、导航、分类框架，**文章和书签需自行添加**。

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
