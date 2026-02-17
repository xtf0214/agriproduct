# 农产品电商系统 - 完整架构设计与实现计划

## 项目进度总览

| 阶段 | 内容 | 状态 | 完成度 |
|------|------|------|--------|
| **阶段一** | 后端-项目初始化 | ✅ 已完成 | 100% |
| **阶段二** | 后端-基础模块开发 | ✅ 已完成 | 100% |
| **阶段三** | 后端-核心业务模块 | ✅ 已完成 | 100% |
| **阶段四** | 后端-商家管理功能 | ✅ 已完成 | 100% |
| **阶段五** | 后端-管理后台功能 | ✅ 已完成 | 100% |
| **阶段六** | 前端-商家端开发 | ❌ 未开始 | 0% |
| **阶段七** | 前端-管理后台开发 | ❌ 未开始 | 0% |
| **阶段八** | 小程序-用户端开发 | ❌ 未开始 | 0% |

### 项目目录结构

| 目录 | 说明 |
|------|------|
| `agriproduct-backend/` | 后端服务（Java Spring Boot） |
| `agriproduct-web/` | Web前端（Vue3） |
| `agriproduct-miniprogram/` | 微信小程序（用户端） |
| `sql/` | 数据库脚本 |

### 已完成服务模块

| 服务 | 端口 | 状态 |
|------|------|------|
| agriproduct-user | 8081 | ✅ 运行正常 |
| agriproduct-product | 8082 | ✅ 运行正常 |
| agriproduct-order | 8083 | ✅ 运行正常 |
| agriproduct-merchant | 8084 | ✅ 运行正常 |
| agriproduct-admin | 8085 | ✅ 运行正常 |

---

## 1. 项目概述

### 1.1 项目背景
构建一个连接农户、商家和消费者的农产品电商平台，包含三个端：
- **用户端（小程序）**：消费者购买农产品
- **商家端（Web）**：商家管理商品和订单
- **管理后台（Web）**：平台审核和管理

### 1.2 技术选型
| 层级 | 技术选型 |
|------|---------|
| 后端框架 | Spring Boot 3.x |
| 数据库 | MySQL 8.0 |
| ORM | MyBatis-Plus |
| 缓存 | Redis |
| 消息队列 | RabbitMQ（可选，用于订单处理） |
| 文件存储 | MinIO / 阿里云OSS |
| 商家/管理端前端 | Vue 3 + Element Plus |
| 用户端 | 微信小程序 |

---

## 2. 系统架构设计

### 2.1 项目整体目录结构

```
agriproduct/                          # 项目根目录
│
├── agriproduct-backend/              # 后端服务（Java Spring Boot）
│   ├── agriproduct-common/          # 公共模块
│   │   ├── agriproduct-common-core/     # 核心工具类
│   │   ├── agriproduct-common-web/      # Web相关配置
│   │   └── agriproduct-common-redis/    # Redis配置
│   │
│   ├── agriproduct-api/             # API接口定义
│   │   ├── agriproduct-api-user/        # 用户服务API
│   │   ├── agriproduct-api-product/     # 商品服务API
│   │   ├── agriproduct-api-order/       # 订单服务API
│   │   └── agriproduct-api-merchant/    # 商家服务API
│   │
│   ├── agriproduct-service/         # 业务服务模块
│   │   ├── agriproduct-user/            # 用户服务（8081）
│   │   ├── agriproduct-product/         # 商品服务（8082）
│   │   ├── agriproduct-order/           # 订单服务（8083）
│   │   ├── agriproduct-merchant/        # 商家服务（8084）
│   │   └── agriproduct-admin/           # 管理后台（8085）
│   │
│   └── pom.xml                      # Maven父POM
│
├── agriproduct-web/                  # Web前端（Vue3）
│   ├── agriproduct-web-merchant/    # 商家端后台
│   └── agriproduct-web-admin/       # 管理后台
│
├── agriproduct-miniprogram/          # 微信小程序（用户端）
│
├── sql/                              # 数据库脚本
│   └── agriproduct.sql
│
├── PLAN.md                           # 项目计划文档
└── .gitignore                        # Git忽略配置
```

### 2.2 核心功能模块

#### 用户端（小程序）功能
| 模块 | 功能 |
|------|------|
| 首页 | 轮播图展示、热门商品推荐、分类导航 |
| 商品 | 商品列表、搜索、详情、加入购物车 |
| 购物车 | 查看、修改数量、删除、结算 |
| 订单 | 订单列表、订单详情、订单状态跟踪 |
| 个人中心 | 个人信息、收货地址管理、订单管理 |

#### 商家端（Web）功能
| 模块 | 功能 |
|------|------|
| 商品管理 | 商品上架、下架、编辑、库存管理 |
| 订单管理 | 订单查看、发货、退款处理 |
| 收益统计 | 销售额、订单量、商品销量统计 |

#### 管理后台（Web）功能
| 模块 | 功能 |
|------|------|
| 商品审核 | 商品审核、驳回、违规处理 |
| 用户管理 | 用户列表、禁用/启用、角色管理 |
| 商家管理 | 商家入驻审核、商家管理 |
| 系统管理 | 轮播图配置、分类管理 |

---

## 3. 数据库设计

### 3.1 核心数据表

```sql
-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像',
    password VARCHAR(200) COMMENT '密码',
    user_type TINYINT DEFAULT 1 COMMENT '用户类型:1-普通用户,2-商家,3-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商家表
CREATE TABLE mer_merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    shop_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    shop_desc VARCHAR(500) COMMENT '店铺描述',
    business_license VARCHAR(500) COMMENT '营业执照',
    status TINYINT DEFAULT 0 COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商品分类表
CREATE TABLE prod_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    icon VARCHAR(500) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE prod_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    category_id BIGINT COMMENT '分类ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(200) COMMENT '副标题',
    main_image VARCHAR(500) COMMENT '主图',
    images TEXT COMMENT '商品图片JSON',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    detail TEXT COMMENT '商品详情HTML',
    status TINYINT DEFAULT 0 COMMENT '状态:0-下架,1-上架,2-审核中',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
    audit_reason VARCHAR(500) COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 轮播图表
CREATE TABLE content_banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) COMMENT '标题',
    image VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_type TINYINT DEFAULT 1 COMMENT '链接类型:1-无,2-商品,3-分类',
    link_id BIGINT COMMENT '关联ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 收货地址表
CREATE TABLE user_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail_address VARCHAR(200) COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认:0-否,1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 购物车表
CREATE TABLE cart_shopping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE order_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status TINYINT DEFAULT 1 COMMENT '状态:1-待付款,2-待发货,3-待收货,4-已完成,5-已取消',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态:0-未支付,1-已支付',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    finish_time DATETIME COMMENT '完成时间',

    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货电话',
    receiver_address VARCHAR(300) NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '订单备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单商品明细表
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) COMMENT '商品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '小计'
);
```

---

## 4. API接口设计

### 4.1 用户端API（小程序）

```
# 首页模块
GET  /api/user/home/banner          # 获取轮播图
GET  /api/user/home/recommend       # 获取推荐商品
GET  /api/user/home/categories      # 获取分类列表

# 商品模块
GET  /api/user/product/list         # 商品列表（支持分页、搜索、分类筛选）
GET  /api/user/product/{id}         # 商品详情
GET  /api/user/product/search       # 商品搜索

# 购物车模块
GET  /api/user/cart                 # 获取购物车列表
POST /api/user/cart                 # 添加购物车
PUT  /api/user/cart/{id}            # 修改购物车数量
DELETE /api/user/cart/{id}          # 删除购物车项

# 订单模块
POST /api/user/order                # 创建订单
GET  /api/user/order/list           # 订单列表
GET  /api/user/order/{id}           # 订单详情
POST /api/user/order/{id}/cancel    # 取消订单

# 用户模块
GET  /api/user/user/profile         # 获取个人信息
PUT  /api/user/user/profile         # 修改个人信息
GET  /api/user/user/addresses       # 收货地址列表
POST /api/user/user/addresses       # 添加收货地址
PUT  /api/user/user/addresses/{id}  # 修改收货地址
DELETE /api/user/user/addresses/{id}# 删除收货地址
```

### 4.2 商家端API（Web）

```
# 商品管理
GET  /api/merchant/product/list     # 商品列表
POST /api/merchant/product          # 添加商品
PUT  /api/merchant/product/{id}     # 修改商品
DELETE /api/merchant/product/{id}   # 删除商品
PUT  /api/merchant/product/{id}/stock  # 修改库存

# 订单管理
GET  /api/merchant/order/list       # 订单列表
GET  /api/merchant/order/{id}       # 订单详情
PUT  /api/merchant/order/{id}/ship  # 订单发货

# 统计分析
GET  /api/merchant/statistics/overview  # 统计概览（销售额、订单量）
GET  /api/merchant/statistics/sales     # 销售统计（按日期）
GET  /api/merchant/statistics/products  # 商品销量排行
```

### 4.3 管理后台API（Web）

```
# 商品审核
GET  /api/admin/product/audit/list   # 待审核商品列表
PUT  /api/admin/product/{id}/audit   # 商品审核（通过/拒绝）

# 用户管理
GET  /api/admin/user/list            # 用户列表
PUT  /api/admin/user/{id}/status     # 修改用户状态

# 商家管理
GET  /api/admin/merchant/list        # 商家列表
PUT  /api/admin/merchant/{id}/audit  # 商家入驻审核

# 轮播图管理
GET  /api/admin/banner/list          # 轮播图列表
POST /api/admin/banner               # 添加轮播图
PUT  /api/admin/banner/{id}          # 修改轮播图
DELETE /api/admin/banner/{id}        # 删除轮播图
```

---

## 5. 实现步骤

### 阶段一：项目初始化 ✅ 已完成
- [x] 创建Maven多模块项目结构
- [x] 配置Spring Boot基础依赖
- [x] 配置MySQL数据库连接
- [x] 配置MyBatis-Plus
- [ ] 创建代码生成器配置（可选）

### 阶段二：基础模块开发 ✅ 已完成
- [x] 公共模块开发
  - [x] 统一响应结果封装
  - [x] 统一异常处理
  - [x] 分页封装
  - [x] 工具类（ID生成、日期处理等）
- [x] 用户认证授权
  - [x] JWT Token实现
  - [x] 登录/注册接口
  - [x] 权限拦截器

### 阶段三：核心业务模块 ✅ 已完成
- [x] 商品服务
  - [x] 商品CRUD
  - [x] 商品搜索
  - [ ] 商品推荐算法（可选）
- [x] 订单服务
  - [x] 购物车管理
  - [x] 订单创建流程
  - [x] 订单状态流转
- [x] 用户服务
  - [x] 用户信息管理
  - [x] 收货地址管理

### 阶段四：商家管理功能 ✅ 已完成
- [x] 商家入驻审核
- [x] 商家商品管理
- [x] 商家订单管理
- [x] 收益统计分析接口

### 阶段五：管理后台功能 ✅ 已完成
- [x] 商品审核
- [x] 用户管理（启用/禁用）
- [x] 轮播图配置
- [x] 商家审核管理

### 阶段六：前端-商家端开发 ❌ 未开始
- [ ] 项目初始化
  - [ ] 创建Vue3项目并安装依赖
  - [ ] 配置Vite、TypeScript、Element Plus
- [ ] 基础架构
  - [ ] 配置路由和布局组件
  - [ ] 封装Axios请求工具
  - [ ] 配置Pinia状态管理
- [ ] 核心功能
  - [ ] 登录页面
  - [ ] 商品管理（列表、添加、编辑）
  - [ ] 订单管理（列表、详情、发货）
  - [ ] 统计分析

### 阶段七：前端-管理后台开发 ❌ 未开始
- [ ] 项目初始化
- [ ] 登录与权限管理
- [ ] 商品审核功能
- [ ] 商家审核功能
- [ ] 用户管理功能
- [ ] 轮播图和分类管理

### 阶段八：小程序-用户端开发 ❌ 未开始
- [ ] 项目初始化
- [ ] 基础组件开发
- [ ] 首页与分类页
- [ ] 商品搜索与详情
- [ ] 购物车功能
- [ ] 订单流程
- [ ] 个人中心

---

## 6. 技术要点

### 6.1 安全性
- 使用BCrypt加密用户密码
- JWT Token进行身份认证
- 接口权限校验
- SQL注入防护（MyBatis预编译）

### 6.2 性能优化
- Redis缓存热门商品数据
- 商品搜索使用ElasticSearch（可选）
- 图片使用CDN加速
- 数据库索引优化

### 6.3 业务规则
- 订单超时自动取消（延时队列）
- 库存扣减使用乐观锁
- 商品审核通过后才能上架
- 商家入驻需管理员审核

---

## 7. 目录结构（单个服务模块）

```
agriproduct-backend/agriproduct-user/
├── src/main/java/com/agriproduct/user/
│   ├── controller/          # 控制器层
│   ├── service/             # 服务层
│   │   └── impl/           # 服务实现
│   ├── mapper/             # MyBatis Mapper
│   ├── entity/             # 数据库实体
│   ├── dto/                # 数据传输对象
│   ├── vo/                 # 视图对象
│   ├── config/             # 配置类
│   └── util/               # 工具类
├── src/main/resources/
│   ├── mapper/             # MyBatis XML
│   ├── application.yml     # 配置文件
│   └── logback.xml         # 日志配置
└── pom.xml
```

---

## 8. 验证计划

### 功能测试
- [ ] 用户注册登录
- [ ] 商品浏览搜索
- [ ] 购物车添加删除
- [ ] 订单创建支付
- [ ] 商家商品管理
- [ ] 订单发货流程
- [ ] 管理员商品审核

### 单元测试（已完成）
- [x] UserService - 用户服务测试
- [x] AddressService - 地址管理测试
- [x] ProductService - 商品服务测试
- [x] CategoryService - 分类服务测试
- [x] BannerService - 轮播图服务测试
- [x] CartService - 购物车服务测试
- [x] OrderService - 订单服务测试

### 接口测试
- 使用Postman/Apifox进行API测试
- 单元测试覆盖率>80%（已完成核心模块测试）

### 性能测试
- 并发下单测试
- 商品搜索响应时间<500ms

---

## 9. 前端开发详细设计

### 9.1 技术栈确认

| 端 | 技术栈 | 说明 |
|---|--------|------|
| 商家端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router | PC端Web应用 |
| 管理后台 | Vue 3 + Vite + Element Plus + Pinia + Vue Router | PC端Web应用 |
| 用户端 | 微信小程序原生开发 | 移动端小程序 |

### 9.2 商家端开发流程

#### 9.2.1 项目初始化

```bash
# 进入Web前端目录
cd d:\Code\Java\agriproduct\agriproduct-web

# 创建项目（如使用Vite命令）
# npm create vite@latest agriproduct-web-merchant -- --template vue-ts
cd agriproduct-web-merchant

# 使用Vite创建Vue3项目
npm create vite@latest . -- --template vue-ts

# 安装依赖
npm install

# 安装核心依赖
npm install element-plus @element-plus/icons-vue
npm install vue-router@4 pinia
npm install axios
npm install dayjs
npm install @vueuse/core
npm install nprogress

# 安装开发依赖
npm install -D sass
npm install -D unplugin-vue-components unplugin-auto-import
```

#### 9.2.2 目录结构设计

```
agriproduct-web/agriproduct-web-merchant/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                    # API接口
│   │   ├── index.ts           # API入口
│   │   ├── product.ts         # 商品相关接口
│   │   ├── order.ts           # 订单相关接口
│   │   ├── statistics.ts      # 统计相关接口
│   │   └── types.ts           # API类型定义
│   │
│   ├── assets/                 # 静态资源
│   │   ├── images/
│   │   └── styles/
│   │       ├── variables.scss # SCSS变量
│   │       ├── reset.scss     # 样式重置
│   │       └── global.scss    # 全局样式
│   │
│   ├── components/             # 公共组件
│   │   ├── Layout/            # 布局组件
│   │   │   ├── index.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Header.vue
│   │   ├── ImageUpload/       # 图片上传组件
│   │   ├── Pagination/        # 分页组件
│   │   └── SearchForm/        # 搜索表单组件
│   │
│   ├── composables/            # 组合式函数
│   │   ├── useTable.ts        # 表格通用逻辑
│   │   ├── useForm.ts         # 表单通用逻辑
│   │   └── useUpload.ts       # 上传通用逻辑
│   │
│   ├── directives/             # 自定义指令
│   │   └── permission.ts      # 权限指令
│   │
│   ├── router/                 # 路由配置
│   │   ├── index.ts
│   │   └── routes.ts
│   │
│   ├── stores/                 # Pinia状态管理
│   │   ├── index.ts
│   │   ├── user.ts            # 用户状态
│   │   └── app.ts             # 应用状态
│   │
│   ├── types/                  # TypeScript类型
│   │   ├── global.d.ts
│   │   └── api.d.ts
│   │
│   ├── utils/                  # 工具函数
│   │   ├── request.ts         # Axios封装
│   │   ├── auth.ts            # Token管理
│   │   ├── storage.ts         # 本地存储
│   │   └── format.ts          # 格式化工具
│   │
│   ├── views/                  # 页面组件
│   │   ├── login/             # 登录页
│   │   │   └── index.vue
│   │   ├── dashboard/         # 首页仪表盘
│   │   │   └── index.vue
│   │   ├── product/           # 商品管理
│   │   │   ├── list.vue       # 商品列表
│   │   │   ├── edit.vue       # 商品编辑
│   │   │   └── components/
│   │   │       └── ProductForm.vue
│   │   ├── order/             # 订单管理
│   │   │   ├── list.vue       # 订单列表
│   │   │   ├── detail.vue     # 订单详情
│   │   │   └── components/
│   │   │       └── OrderStatus.vue
│   │   ├── statistics/        # 统计分析
│   │   │   ├── index.vue      # 统计概览
│   │   │   └── components/
│   │   │       ├── SalesChart.vue
│   │   │       └── ProductRank.vue
│   │   └── profile/           # 个人中心
│   │       └── index.vue
│   │
│   ├── App.vue                 # 根组件
│   ├── main.ts                 # 入口文件
│   └── env.d.ts
│
├── .env                        # 环境变量
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── vite.config.ts              # Vite配置
├── tsconfig.json               # TypeScript配置
└── package.json
```

#### 9.2.3 核心配置文件

**vite.config.ts**
```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts'
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts'
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:8084',
        changeOrigin: true
      }
    }
  }
})
```

**环境变量 .env.development**
```env
VITE_APP_TITLE=农产品商家后台
VITE_API_BASE_URL=http://localhost:8084
VITE_UPLOAD_URL=http://localhost:8084/api/merchant/upload
```

#### 9.2.4 页面开发计划

| 优先级 | 页面 | 路由 | 功能描述 |
|--------|------|------|----------|
| P0 | 登录页 | /login | 商家登录、Token存储 |
| P0 | 首页仪表盘 | /dashboard | 销售额、订单量、商品统计 |
| P0 | 商品列表 | /product/list | 商品查看、上下架、编辑、删除 |
| P0 | 商品添加/编辑 | /product/edit | 商品信息表单、图片上传 |
| P0 | 订单列表 | /order/list | 订单查看、筛选、发货 |
| P0 | 订单详情 | /order/detail | 订单商品、收货信息、操作 |
| P1 | 统计概览 | /statistics | 销售图表、商品排行 |
| P1 | 个人中心 | /profile | 密码修改、店铺信息 |

#### 9.2.5 开发任务分解

**第一阶段：项目基础搭建（2天）**
```
□ Day 1
  ├─□ 创建Vue3项目并安装依赖
  ├─□ 配置Vite、TypeScript、Element Plus
  ├─□ 配置路由守卫和基础路由
  └─□ 封装Axios请求工具

□ Day 2
  ├─□ 创建布局组件（侧边栏、头部）
  ├─□ 配置Pinia状态管理
  ├─□ 封装通用组件（上传、分页）
  └─□ 配置API接口模块
```

**第二阶段：核心功能开发（5天）**
```
□ Day 3 - 登录与首页
  ├─□ 登录页面开发
  ├─□ 登录接口对接
  └─□ 首页仪表盘开发

□ Day 4-5 - 商品管理
  ├─□ 商品列表页面
  ├─□ 商品添加/编辑表单
  ├─□ 图片上传功能
  └─□ 商品上下架功能

□ Day 6-7 - 订单管理
  ├─□ 订单列表页面
  ├─□ 订单详情页面
  ├─□ 订单发货功能
  └─□ 订单状态筛选
```

**第三阶段：完善与优化（2天）**
```
□ Day 8
  ├─□ 统计分析页面
  ├─□ 图表组件集成
  └─□ 个人中心页面

□ Day 9
  ├─□ 整体样式优化
  ├─□ 响应式适配
  └─□ 性能优化（懒加载、缓存）
```

---

### 9.3 管理后台开发流程

#### 9.3.1 项目初始化

```bash
# 进入Web前端目录
cd d:\Code\Java\agriproduct\agriproduct-web

# 创建项目（如使用Vite命令）
# npm create vite@latest agriproduct-web-admin -- --template vue-ts
cd agriproduct-web-admin

# 使用Vite创建Vue3项目
npm create vite@latest . -- --template vue-ts

# 安装依赖（同商家端）
npm install element-plus @element-plus/icons-vue vue-router@4 pinia axios dayjs @vueuse/core nprogress
npm install -D sass unplugin-vue-components unplugin-auto-import
```

#### 9.3.2 目录结构设计

```
agriproduct-web/agriproduct-web-admin/
├── public/
├── src/
│   ├── api/
│   │   ├── auth.ts            # 认证接口
│   │   ├── user.ts            # 用户管理接口
│   │   ├── merchant.ts        # 商家管理接口
│   │   ├── product.ts         # 商品审核接口
│   │   ├── banner.ts          # 轮播图接口
│   │   └── category.ts        # 分类管理接口
│   │
│   ├── components/
│   │   ├── Layout/
│   │   ├── ImageUpload/
│   │   └── Permission/
│   │
│   ├── router/
│   │   ├── index.ts
│   │   └── routes.ts
│   │
│   ├── stores/
│   │   ├── user.ts
│   │   └── permission.ts      # 权限状态
│   │
│   └── views/
│       ├── login/
│       ├── dashboard/
│       ├── user/              # 用户管理
│       │   └── list.vue
│       ├── merchant/          # 商家管理
│       │   ├── list.vue
│       │   └── audit.vue
│       ├── product/           # 商品审核
│       │   ├── audit.vue
│       │   └── detail.vue
│       ├── banner/            # 轮播图管理
│       │   └── list.vue
│       └── category/          # 分类管理
│           └── list.vue
│
├── .env.development
├── vite.config.ts
└── package.json
```

#### 9.3.3 页面开发计划

| 优先级 | 页面 | 路由 | 功能描述 |
|--------|------|------|----------|
| P0 | 登录页 | /login | 管理员登录 |
| P0 | 首页仪表盘 | /dashboard | 平台数据概览 |
| P0 | 商品审核 | /product/audit | 商品列表、审核操作 |
| P0 | 商家审核 | /merchant/audit | 商家入驻审核 |
| P0 | 用户管理 | /user/list | 用户列表、禁用/启用 |
| P1 | 轮播图管理 | /banner/list | 轮播图CRUD |
| P1 | 分类管理 | /category/list | 分类CRUD |

#### 9.3.4 开发任务分解

**第一阶段：项目基础搭建（2天）**
```
□ Day 1
  ├─□ 创建项目并配置依赖
  ├─□ 配置路由和布局
  └─□ 封装请求工具

□ Day 2
  ├─□ 登录功能开发
  ├─□ 权限管理实现
  └─□ 通用组件封装
```

**第二阶段：核心功能开发（4天）**
```
□ Day 3 - 商品审核
  ├─□ 商品审核列表
  ├─□ 商品详情查看
  └─□ 审核通过/拒绝功能

□ Day 4 - 商家审核
  ├─□ 商家列表页面
  └─□ 商家入驻审核

□ Day 5 - 用户管理
  ├─□ 用户列表页面
  └─□ 用户状态管理

□ Day 6 - 内容管理
  ├─□ 轮播图管理
  └─□ 分类管理
```

**第三阶段：完善优化（1天）**
```
□ Day 7
  ├─□ 首页数据统计
  ├─□ 样式优化
  └─□ 测试修复
```

---

### 9.4 用户端小程序开发流程

#### 9.4.1 项目初始化

```bash
# 使用微信开发者工具创建项目
# 项目目录: d:\Code\Java\agriproduct\agriproduct-miniprogram
# AppID: 使用测试号或正式AppID
```

#### 9.4.2 目录结构设计

```
agriproduct-miniprogram/
├── miniprogram/
│   ├── api/                    # API接口
│   │   ├── index.js           # API配置
│   │   ├── home.js            # 首页接口
│   │   ├── product.js         # 商品接口
│   │   ├── cart.js            # 购物车接口
│   │   ├── order.js           # 订单接口
│   │   └── user.js            # 用户接口
│   │
│   ├── components/             # 自定义组件
│   │   ├── ProductCard/       # 商品卡片
│   │   ├── CartItem/          # 购物车项
│   │   ├── OrderItem/         # 订单项
│   │   ├── LoadMore/          # 加载更多
│   │   └── Empty/             # 空状态
│   │
│   ├── pages/                  # 页面
│   │   ├── index/             # 首页
│   │   │   ├── index.js
│   │   │   ├── index.json
│   │   │   ├── index.wxml
│   │   │   └── index.wxss
│   │   ├── category/          # 分类页
│   │   ├── cart/              # 购物车
│   │   ├── product/           # 商品详情
│   │   │   └── detail/
│   │   ├── order/             # 订单相关
│   │   │   ├── list/          # 订单列表
│   │   │   └── detail/        # 订单详情
│   │   └── user/              # 个人中心
│   │       ├── index/         # 个人中心首页
│   │       └── address/       # 地址管理
│   │
│   ├── subpackages/            # 分包
│   │   └── order/             # 订单分包
│   │
│   ├── utils/                  # 工具函数
│   │   ├── request.js         # 请求封装
│   │   ├── auth.js            # 登录认证
│   │   └── util.js            # 通用工具
│   │
│   ├── app.js                  # 小程序入口
│   ├── app.json                # 全局配置
│   ├── app.wxss                # 全局样式
│   └── sitemap.json
│
└── project.config.json         # 项目配置
```

#### 9.4.3 页面开发计划

| 优先级 | 页面 | 路径 | 功能描述 |
|--------|------|------|----------|
| P0 | 首页 | /pages/index | 轮播图、推荐商品、分类入口 |
| P0 | 分类页 | /pages/category | 分类列表、分类商品 |
| P0 | 商品详情 | /pages/product/detail | 商品信息、加入购物车、立即购买 |
| P0 | 购物车 | /pages/cart | 购物车列表、数量修改、结算 |
| P0 | 订单确认 | /pages/order/confirm | 地址选择、订单提交 |
| P0 | 订单列表 | /pages/order/list | 全部订单、待付款、待收货 |
| P0 | 订单详情 | /pages/order/detail | 订单信息、操作按钮 |
| P0 | 个人中心 | /pages/user/index | 用户信息、功能入口 |
| P0 | 地址管理 | /pages/user/address | 地址列表、添加、编辑 |
| P1 | 商品搜索 | /pages/search | 搜索、搜索历史 |

#### 9.4.4 小程序配置

**app.json**
```json
{
  "pages": [
    "pages/index/index",
    "pages/category/category",
    "pages/cart/cart",
    "pages/user/index/index"
  ],
  "subpackages": [
    {
      "root": "subpackages/order",
      "pages": [
        "confirm/confirm",
        "list/list",
        "detail/detail"
      ]
    }
  ],
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#07C160",
    "backgroundColor": "#ffffff",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页",
        "iconPath": "assets/icons/home.png",
        "selectedIconPath": "assets/icons/home-active.png"
      },
      {
        "pagePath": "pages/category/category",
        "text": "分类",
        "iconPath": "assets/icons/category.png",
        "selectedIconPath": "assets/icons/category-active.png"
      },
      {
        "pagePath": "pages/cart/cart",
        "text": "购物车",
        "iconPath": "assets/icons/cart.png",
        "selectedIconPath": "assets/icons/cart-active.png"
      },
      {
        "pagePath": "pages/user/index/index",
        "text": "我的",
        "iconPath": "assets/icons/user.png",
        "selectedIconPath": "assets/icons/user-active.png"
      }
    ]
  },
  "window": {
    "navigationBarTitleText": "农产品商城",
    "navigationBarBackgroundColor": "#FFFFFF",
    "navigationBarTextStyle": "black"
  },
  "requestDomain": [
    "http://localhost:8081"
  ]
}
```

#### 9.4.5 开发任务分解

**第一阶段：基础架构（2天）**
```
□ Day 1
  ├─□ 创建小程序项目
  ├─□ 配置TabBar和路由
  ├─□ 封装请求工具
  └─□ 登录认证流程

□ Day 2
  ├─□ 公共组件开发
  │   ├─□ ProductCard商品卡片
  │   ├─□ LoadMore加载更多
  │   └─□ Empty空状态
  └─□ 全局样式配置
```

**第二阶段：核心页面（5天）**
```
□ Day 3 - 首页
  ├─□ 轮播图组件
  ├─□ 分类导航
  └─□ 推荐商品列表

□ Day 4 - 分类与搜索
  ├─□ 分类页面
  ├─□ 商品列表
  └─□ 搜索功能

□ Day 5 - 商品详情
  ├─□ 商品图片轮播
  ├─□ 商品信息展示
  └─□ 加入购物车/立即购买

□ Day 6 - 购物车
  ├─□ 购物车列表
  ├─□ 数量修改
  └─□ 结算功能

□ Day 7 - 订单流程
  ├─□ 订单确认页
  ├─□ 订单提交
  └─□ 支付流程（模拟）
```

**第三阶段：订单与个人中心（3天）**
```
□ Day 8 - 订单管理
  ├─□ 订单列表
  ├─□ 订单详情
  └─□ 取消订单

□ Day 9 - 个人中心
  ├─□ 个人信息展示
  ├─□ 地址管理
  └─□ 登录授权

□ Day 10 - 优化测试
  ├─□ 性能优化
  ├─□ 样式调整
  └─□ 功能测试
```

---

### 9.5 前端开发时间线

| 阶段 | 内容 | 天数 | 开始日期 | 结束日期 | 状态 |
|------|------|------|----------|----------|------|
| 商家端基础 | 项目搭建+登录+布局 | 2 | - | - | ❌ |
| 商家端核心 | 商品+订单管理 | 5 | - | - | ❌ |
| 商家端完善 | 统计+优化 | 2 | - | - | ❌ |
| 管理端基础 | 项目搭建+登录 | 2 | - | - | ❌ |
| 管理端核心 | 审核+管理功能 | 4 | - | - | ❌ |
| 管理端完善 | 优化测试 | 1 | - | - | ❌ |
| 小程序基础 | 架构+组件 | 2 | - | - | ❌ |
| 小程序核心 | 首页+商品+购物车 | 5 | - | - | ❌ |
| 小程序完善 | 订单+个人中心 | 3 | - | - | ❌ |

**总计：约26个工作日**

---

### 9.6 前端开发注意事项

#### 9.6.1 API对接规范
- 所有API请求统一通过封装的request工具
- 自动携带Token到请求头
- 统一处理401未授权跳转登录
- 统一错误提示处理

#### 9.6.2 状态管理规范
- 用户信息、Token存储在Pinia/localStorage
- 购物车数据可选择本地存储或服务端同步
- 避免过多共享状态，优先组件内管理

#### 9.6.3 样式规范
- 采用统一的设计规范（主色、字体、间距）
- Element Plus主题定制
- 小程序采用rpx单位适配多屏幕

#### 9.6.4 性能优化
- 图片懒加载
- 路由懒加载
- 列表虚拟滚动（长列表）
- 小程序分包加载
- 请求防抖节流

---

## 10. 部署计划

### 10.1 后端部署
```
□ 打包各服务JAR包
□ 配置生产环境数据库
□ 配置Nginx反向代理
□ 配置HTTPS证书
□ 启动各微服务
```

### 10.2 前端部署
```
□ Web端执行npm run build
□ 上传dist到Nginx服务器
□ 配置前端路由history模式
□ 小程序提交审核发布
```

### 10.3 服务器配置建议
| 服务 | 配置 | 数量 |
|------|------|------|
| 应用服务器 | 2核4G | 2 |
| 数据库服务器 | 4核8G | 1 |
| Redis | 1核2G | 1 |
| 对象存储 | 按需 | - |
