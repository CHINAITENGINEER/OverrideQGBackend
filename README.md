# 宿舍报修管理系统

> 前后端分离目录：`frontend/` 静态页面 + `backend/` Spring Boot REST API；生产环境可用 `nginx/nginx.conf` 同域反代。

## 目录结构

```
dormitory-repair/
├── backend/                 # 后端（Maven 工程）
│   ├── pom.xml
│   ├── sql/init.sql         # 数据库初始化
│   └── src/main/            # Java + application.yml + MyBatis XML
├── frontend/                # 前端（纯静态：index.html / css / js）
├── nginx/
│   └── nginx.conf           # 示例：静态 + /api 反代 8080
├── docs/                    # 说明文档（可选）
└── README.md
```

## 环境

| 工具   | 说明        |
|--------|-------------|
| JDK 21 | 后端编译运行 |
| Maven  | 3.8+        |
| MySQL  | 8.x         |
| Nginx  | 可选，用于部署 |

## 1. 初始化数据库

执行 `backend/sql/init.sql`（建库、建表、可选测试账号）。

## 2. 配置后端

编辑 `backend/src/main/resources/application.yml` 中的数据源账号密码。

## 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认监听 `http://127.0.0.1:8080`，API 前缀为 `/api/...`。

## 4. 前端访问方式

**方式 A — Nginx（推荐，与线上一致）**

在 `dormitory-repair` 目录下（使 `frontend/` 为相对路径）：

```bash
nginx -p "%CD%" -c nginx/nginx.conf
```

浏览器访问 `http://localhost/`，前端请求 `/api/...` 由 Nginx 转发到本机 8080。

**方式 B — 直连后端调试**

将 `frontend/js/app.js` 中 `API_ROOT` 设为 `'http://127.0.0.1:8080'`，用本地静态服务器打开 `frontend/index.html`（需处理浏览器 CORS；后端已配置常见本地域名的 CORS）。

## 5. 可选：控制台菜单

在 `application.yml` 中设置 `dormitory-repair.console.enabled: true` 后启动后端，可在终端使用原控制台菜单（与 Web 并存）。

---

历史文档见 `docs/` 目录（部分内容可能早于当前 Spring Boot 结构，以 `backend/` 代码为准）。
## 6.一些图片展示
<img width="2157" height="1340" alt="image" src="https://github.com/user-attachments/assets/1526c1d4-3fb4-4265-a4b3-16b09081d02b" />
<img width="2158" height="1431" alt="image" src="https://github.com/user-attachments/assets/22128553-a4a4-44aa-b323-08fcdaa912e4" />
<img width="2156" height="1313" alt="image" src="https://github.com/user-attachments/assets/1cfef03a-8b7c-460c-9b44-40bbb9d3c44a" />
<img width="2159" height="1076" alt="image" src="https://github.com/user-attachments/assets/700a98cd-a050-426d-a92f-95f79fef3abc" />






