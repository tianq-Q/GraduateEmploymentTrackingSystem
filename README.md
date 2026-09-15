# 高校毕业生就业跟踪与分析系统

基于 Spring Boot 2.7 + Vue 3 的 DDD 分层架构全栈项目，实现毕业生就业信息采集、审核、统计分析的完整闭环。

## 功能模块

| 模块 | 说明 |
|------|------|
| 用户认证 | 登录/注册、JWT 鉴权、基于角色的路由与数据隔离 |
| 基础数据管理 | 院系 → 专业 → 班级 → 毕业生 + 教师账号维护 |
| 就业采集审核 | 毕业生填报、教师代录、教师审核 + 校级终审二级审批链 |
| 数据仪表板 | 就业率统计卡片、去向饼图、院系柱图、行业分布 |
| 求职中心 | 就业信息登记、审核历史查询、求职功能预留 |
| 系统管理 | 用户管理、操作/登录日志（仅系统管理员） |

## 角色

| 角色 | 说明 |
|------|------|
| `GRADUATE` | 毕业生，仅个人就业信息操作 |
| `TEACHER` | 就业教师，管理本院系学生与就业审核 |
| `COLLEGE_ADMIN` | 校级管理员，基础数据与终审权限 |
| `SYSTEM_ADMIN` | 系统管理员，含用户与日志管理 |

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Spring Boot 2.7 · MyBatis-Plus 3.5 · MySQL 8 · JWT (jjwt 0.11) · Spring Security · AOP |
| 前端 | Vue 3 · TypeScript · Vite 5 · Element Plus · ECharts 5 · Pinia · Axios |

## 项目结构

```
employment-tracking/
├── backend/                     # Spring Boot 后端 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/college/employment/
│       ├── common/              # 通用层（枚举、日志、工具）
│       ├── config/              # 配置（安全、文件上传等）
│       ├── controller/          # 控制器
│       ├── application/         # 应用服务
│       ├── domain/              # 领域层（模型、仓储、策略）
│       └── infrastructure/      # 基础设施（Mapper、审计、通知、文件存储）
├── frontend/                    # Vue 3 前端
│   └── src/
│       ├── api/                 # 接口层
│       ├── router/              # 路由
│       ├── stores/              # Pinia 状态
│       ├── components/          # 通用组件
│       └── views/               # 页面（auth / student / teacher / admin / profile / dashboard）
├── db/                          # 数据库脚本 + ER 图
├── docs/                        # 需求、架构、接口文档
└── README.md
```

## 快速开始

### 1. 数据库

在 MySQL 8 中执行 `db/scripts/` 下的初始化脚本，并按需修改 `backend/src/main/resources/application*.yml` 中的连接配置。

### 2. 后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

### 3. 前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173 ，默认后端接口地址 http://localhost:8080。

## 一键启动（Windows）

项目根目录提供批处理脚本：

| 脚本 | 说明 |
|------|------|
| `start.bat` | 检查 MySQL 并一键启动前后端 |
| `run-backend.bat` | 仅启动后端（有 jar 直接运行，否则 `mvn spring-boot:run`）|
| `run-frontend.bat` | 仅启动前端 |
| `build.bat` | Maven 打包后端 jar |

## 分支说明

- `main`：主分支，全部代码所在
