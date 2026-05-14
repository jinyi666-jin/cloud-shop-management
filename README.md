# 云电商管理系统

全栈电商后台管理系统，涵盖商品管理、分类管理、订单管理、用户管理和数据仪表盘五大核心模块。

## 技术栈

- **后端**: Java 21 / Spring Boot 2.7 / Spring Security / Spring Data JPA
- **前端**: Vue 3 / Vite 5 / Pinia / Element Plus / Axios
- **数据库**: MySQL 8.0
- **AI集成**: DeepSeek API

## 功能特性

### 核心模块
- 📦 **商品管理**: 商品CRUD、多条件搜索、分类关联
- 🏷️ **分类管理**: 商品分类层级管理
- 📋 **订单管理**: 订单状态跟踪、订单详情查看
- 👥 **用户管理**: 用户信息管理、权限控制
- 📊 **数据仪表盘**: 销售统计、数据可视化

### AI智能助手
- 💬 智能问答服务（集成DeepSeek大语言模型）
- 🎯 商品推荐
- 📦 订单查询
- 🔍 通用知识问答

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+

### 后端启动

```bash
cd backend
# 配置数据库连接 (application.yml)
# 创建数据库 cloud_shop
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

### 访问地址
- 前端: http://localhost:3000
- 后端API: http://localhost:8088

### 默认账号
- 用户名: admin
- 密码: admin123

## 项目结构

```
云电商系统/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/cloudshop/
│   │   ├── controller/         # REST API 控制器
│   │   ├── service/            # 业务逻辑层
│   │   ├── repository/         # 数据访问层
│   │   ├── entity/             # 数据库实体
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       └── application.yml     # 应用配置
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── api/                # API 请求封装
│   │   └── router/             # 路由配置
│   └── vite.config.js          # Vite 配置
└── README.md
```

## 安全特性

- ✅ JWT 无状态认证
- ✅ BCrypt 密码加密
- ✅ CORS 跨域配置
- ✅ API 接口权限控制

## License

MIT License