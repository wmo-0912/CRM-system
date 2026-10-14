# CRM 客户关系管理系统

> 一个前后端分离的**企业级客户关系管理（CRM）系统模拟项目**，实现对客户信息、服务管理、工单跟进等核心流程的信息化管理，帮助提升销售与服务效率。

---

## ✨ 功能特性

### 用户 / 认证
- 用户注册、登录（密码使用 **BCrypt** 加密存储）
- 双角色体系：**管理员** 与 **客户**
- 注册为客户时自动创建对应客户档案

### 管理后台（管理员）
- **客户管理**：客户的增删改查，管理联系方式、所属行业、状态、备注等
- **用户管理**：系统用户信息维护
- **服务管理**：服务项目的维护（名称、编码、价格、时长、状态等）
- **工单管理**：服务工单的全流程跟踪（优先级、状态、预计/实际起止时间、反馈、退单原因等）

### 客户工作台（客户）
- 客户信息维护、个人资料
- 在线提交服务申请 / 服务工单
- 查看工单进度与反馈

---

## 🛠 技术栈

| 端 | 技术 | 说明 |
| --- | --- | --- |
| 后端 | Spring Boot 3.5.3 | Web 框架 |
| 后端 | Java 24 | 运行环境 |
| 后端 | MyBatis-Plus 3.5.12 | ORM 持久层 |
| 后端 | MySQL | 数据库 |
| 后端 | Druid 1.1.0 | 数据库连接池 |
| 后端 | JBCrypt 0.4 | 密码加密 |
| 后端 | Lombok | 简化实体代码 |
| 前端 | Vue 3 | 前端框架 |
| 前端 | Vite | 构建工具 |
| 前端 | Element Plus | UI 组件库 |
| 前端 | Pinia | 状态管理 |
| 前端 | Vue Router | 前端路由 |
| 前端 | Axios | HTTP 请求 |

---

## 📁 项目结构

```
CRM
├── src/main/java/com/wangmo/crm
│   ├── controller/       # 接口控制层（Customers/Services/Users/WorkOrders）
│   ├── service/          # 业务接口 + impl 实现
│   ├── dao/              # MyBatis-Plus Mapper
│   ├── entity/           # 实体类（对应数据表）
│   ├── config/           # 配置（如跨域 CorsConfig）
│   ├── result/           # 统一返回结果封装
│   └── CrmApplication.java
├── src/main/resources/application.yml   # 后端配置（端口、数据源）
├── vue-mypro/            # 前端工程（Vue 3）
│   └── src/views/        # 页面：登录/注册/管理后台/客户工作台等
├── pom.xml               # Maven 依赖与构建配置
└── README.md
```

### 数据表（对应实体）
| 实体 | 表名 | 主要字段 |
| --- | --- | --- |
| `Users` | `users` | username、password、real_name、department、position、status（1=客户/管理员） |
| `Customers` | `customers` | name、contact_person、phone、email、address、industry、status、remark |
| `Services` | `services` | name、code、description、price、duration、status |
| `WorkOrders` | `work_orders` | order_no、customer_id、service_id、description、priority、status、起止时间、feedback、return_reason |

---

## 🚀 快速开始

### 环境要求
- JDK **24**+
- Maven（或使用项目自带的 `mvnw`）
- Node.js **18**+ 与 npm
- MySQL **8.x**

### 1. 数据库准备
创建数据库并建表（表结构与 `entity` 包中的实体类字段一一对应，如 `users`、`customers`、`services`、`work_orders`）：

```sql
CREATE DATABASE IF NOT EXISTS crm DEFAULT CHARACTER SET utf8mb4;
```

> 具体建表语句请参照实体类字段定义生成。若已有初始化 SQL，可直接导入。

### 2. 启动后端（端口 8080）
1. 修改 `src/main/resources/application.yml` 中的数据库连接信息（URL、用户名、密码）：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/crm?serverTimezone=GMT%2B8&useSSL=FALSE
       username: root
       password: 你的数据库密码
   ```
2. 启动应用：
   ```bash
   # 方式一：使用 Maven 包装器
   ./mvnw spring-boot:run
   # 方式二：直接打包运行
   mvn clean package -DskipTests
   java -jar target/CRM-0.0.1-SNAPSHOT.jar
   ```

### 3. 启动前端（端口 5173）
```bash
cd vue-mypro
npm install        # 安装依赖
npm run dev        # 启动开发服务器
```
浏览器访问 **http://localhost:5173** 即可。

> 前后端跨域已在后端 `CorsConfig` / 各 Controller 的 `@CrossOrigin` 中处理，前端默认代理至 `http://localhost:8080`。

---

## 🔑 使用说明
- 首次使用请先 **注册** 账号，注册时选择角色（客户 / 管理员）。
- 登录时需选择与账号注册一致的角色。
- 管理员登录后可进入 `/admin` 管理后台；客户登录后进入客户工作台。

---

## 📦 构建与部署
```bash
# 后端构建
mvn clean package

# 前端构建
cd vue-mypro && npm run build
```
前端构建产物输出到 `vue-mypro/dist/`，可配合 Nginx 等静态服务器部署，并反向代理 `/api`（或对应接口前缀）到后端服务。

---

## 🗂 常用接口（示例）
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/user/register` | 注册 |
| POST | `/user/login` | 登录 |
| GET | `/user/userlist` | 用户列表 |
| POST | `/user/adduser` | 新增用户 |
| POST | `/user/update` | 修改用户 |
| POST | `/user/delete` | 删除用户 |

> 完整接口请查看 `controller` 包下对应类。

---

## ⚠️ 注意事项
- 当前为**演示/学习用途**项目，登录未集成 JWT/Token 会话机制，接口参数校验较简略，实际生产需补齐鉴权、权限控制与参数校验。
- `application.yml` 中默认配置了本地数据库连接，**请勿将含真实密码的配置文件直接推送到公开仓库**，建议使用环境变量或 `.gitignore` 忽略敏感配置。

---

## 📄 许可证
本项目为个人学习/演示项目，未指定开源许可证。
