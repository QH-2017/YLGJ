# 医疗管家健康管理系统（YLGJ）

> 基于 **Spring Boot + Vue 3** 的前后端分离体检预约与健康管理平台，覆盖**管理后台**与**移动端 H5** 双端业务，内置 JWT 安全认证、Redis 缓存、Quartz 定时任务与 AI 健康助手。

## ✨ 项目亮点

- 🔐 **JWT + Spring Security + Redis 认证体系**：无状态令牌认证、BCrypt 密码加密、登录失败锁定、退出/改密后令牌即时失效
- ⚡ **Redis 缓存落地**：登录令牌管理、登录失败计数、套餐热点数据缓存
- ⏰ **Quartz 定时任务**：每日自动清理过期体检订单
- 🤖 **AI 健康助手**：对接 DeepSeek 大模型，提供智能健康咨询与本地兜底回复
- 📊 **运营报表**：会员/预约统计、热门套餐 Top5、Excel 一键导出
- 📱 **多端覆盖**：Vue3 管理后台 + 原生 H5 移动端
- 🐳 **容器化部署**：Docker Compose 一键启动 MySQL + Redis + 后端
- 🧪 **工程化保障**：JUnit + Mockito 单元测试覆盖核心链路（密码安全 / 下单事务 / 异常出口），GitHub Actions 自动构建
- 🛡️ **统一异常出口**：`@RestControllerAdvice` 全局异常处理 + 业务异常 `BizException`，Controller 不再散落 try/catch
- ⚡ **报表 SQL 聚合**：运营统计由"全表拉取 + 内存聚合"重构为 SQL `COUNT` / `GROUP BY`，随数据量增长更稳
- 🎫 **预约防超卖**：基于每日排班名额 + 数据库条件更新原子占号，下单 / 改期自动占号、取消自动释放名额，杜绝超售

## 🛠️ 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Java 17 · Spring Boot 2.4 · Spring Security · MyBatis-Plus · MySQL · Redis · Quartz · JWT (jjwt) · Apache POI |
| 管理后台 | Vue 3 · TypeScript · Vite · Element Plus · Pinia · Vue Router · ECharts |
| 移动端 | 原生 HTML5 + jQuery / CDN Vue 2 + Element UI |

## 🧩 功能模块

### 管理后台（`/admin`）
- 📈 数据概览仪表盘（ECharts 统计图表）
- 🧬 体检套餐 / 检查组 / 检查项管理（含图片上传）
- 👥 会员管理、预约订单管理、预约条件（排班）设置
- 📑 运营报表统计 + Excel 导出中心
- ⚙️ 系统用户管理、健康资讯管理、个人中心

### 移动端 H5（`/mobile`）
- 🏠 首页、体检套餐浏览/详情、在线预约
- 📄 报告查询（按手机号）、健康档案、健康评估
- 🤖 AI 健康咨询

## 🏗️ 项目结构

```
YLGJ/
├── sql/                        # 数据库初始化脚本（15 张表）
├── .env.example                # 环境变量模板
├── Dockerfile / docker-compose.yml
├── src/main/java/com/ylgj/
│   ├── config/                 # Security / Redis / Quartz / MyBatis-Plus 配置
│   ├── security/               # JWT 工具、认证过滤器、登录用户模型
│   ├── controller/             # REST 接口
│   ├── service/                # 业务逻辑
│   ├── mapper/                 # MyBatis-Plus Mapper
│   ├── pojo/                   # 实体类
│   ├── util/                   # Redis 工具类
│   └── job/                    # Quartz 定时任务
├── src/main/resources/
│   ├── application.yml         # 应用配置（敏感信息环境变量化）
│   └── static/mobile/          # 移动端静态页面（后端托管）
├── ylgj-vue/                   # Vue3 管理后台前端
└── mobile/                     # 移动端 H5 源码
```

## 🚀 快速开始

### 环境要求
- JDK 17+、Maven 3.8+
- MySQL 8.0+、Redis 6+
- Node.js 18+（前端）

### 1. 初始化数据库
```bash
mysql -uroot -p < sql/ylgj.sql
```

### 2. 配置环境变量（可选，均有默认值）
复制 `.env.example` 中的变量，或在系统环境变量中设置：
```bash
DB_PASSWORD=你的数据库密码
DEEPSEEK_API_KEY=你的DeepSeek Key   # AI 助手，不填则走本地兜底回复
JWT_SECRET=自定义密钥
```

### 3. 启动后端
```bash
mvnw spring-boot:run
# 或打包运行
mvnw clean package -DskipTests
java -jar target/ylgj-1.0-SNAPSHOT.jar
```

### 4. 启动管理后台
```bash
cd ylgj-vue
npm install
npm run dev
```

### 5. 访问入口

| 入口 | 地址 | 说明 |
|---|---|---|
| 管理后台 | http://localhost:5173 | 默认账号 `admin / 123456` |
| 后端接口 | http://localhost:8080 | REST API |
| 移动端 | http://localhost:8080/mobile/ | 手机也可访问 |

> **端口冲突**：后端端口（`server.port`，默认 8080）与前端端口（`VITE_PORT`，默认 5173）均支持环境变量覆盖；前端后端地址通过 `VITE_BACKEND_URL` 配置（默认 `http://localhost:8080`），支持 Vite 代理。

## 🐳 Docker 部署
```bash
# 一键启动 MySQL + Redis + 后端
docker compose up -d
```

## 🔐 安全设计
- 密码 **BCrypt** 加盐哈希存储，历史明文密码登录时自动平滑升级
- **JWT** 无状态认证 + Redis 令牌双校验，登出/改密后令牌立即失效
- 连续登录失败 5 次锁定账号 10 分钟（Redis 计数）
- 敏感配置（数据库密码、AI Key、JWT 密钥）通过环境变量注入，不落盘
- 接口匿名白名单与需认证接口分离，401/403 统一返回 JSON

## ⏰ 定时任务
| 任务 | 频率 | 说明 |
|---|---|---|
| 订单过期扫描 | 每天 01:00 | 将预约日期已过且未到诊的订单自动标记为"已过期" |

## 📄 许可证
仅供学习交流使用。
