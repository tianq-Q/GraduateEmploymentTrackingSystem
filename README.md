# 高校毕业生就业跟踪与分析系统

基于 Spring Boot 3.x + Vue 3 的 DDD 分层架构全栈项目。

## 模块速览

| 编号  | 模块          | 说明                                     |
|-------|---------------|------------------------------------------|
| FR-01 | 用户认证      | 登录/登出、JWT、角色路由、数据隔离       |
| FR-02 | 基础数据管理  | 院系 → 专业 → 班级 → 毕业生 + 教师账号   |
| FR-03 | 就业采集审核  | 学生填报 + 教师代录 + 二级审核审批链     |
| FR-04 | 数据仪表板    | 就业率卡片、去向饼图、院系柱图、行业分布 |

## 角色

| 角色   | 说明                     |
|--------|--------------------------|
| ADMIN  | 校级管理员，全部权限     |
| TEACHER | 就业教师，本院系数据范围 |
| STUDENT | 毕业生，仅个人操作       |

## 技术栈

| 端     | 技术                                                |
|--------|-----------------------------------------------------|
| 后端   | Spring Boot 3 · MyBatis-Plus · MySQL 8 · JWT · Security |
| 前端   | Vue 3 · TypeScript · Vite · Element Plus · ECharts 5  |

## 项目结构

```
employment-tracking/
├── backend/                     # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/college/employment/
│       ├── common/              # 通用层
│       ├── config/              # 配置
│       ├── controller/          # 控制器
│       ├── application/         # 应用服务
│       ├── domain/              # 领域层
│       └── infrastructure/      # 基础设施
├── frontend/                    # Vue 3 前端
│   └── src/
│       ├── api/                 # 接口层
│       ├── router/              # 路由
│       ├── stores/              # Pinia 状态
│       ├── components/          # 组件
│       └── views/               # 页面
├── db/                          # 数据库脚本
└── docs/                        # 文档
```

## 快速开始

```bash
# 后端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```
