# 农产品电商系统 - 完整架构设计与实现计划

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

### 2.1 项目模块划分

```
agriproduct/
├── agriproduct-common/          # 公共模块
│   ├── agriproduct-common-core/     # 核心工具类
│   ├── agriproduct-common-web/      # Web相关配置
│   ├── agriproduct-common-security/ # 安全模块
│   └── agriproduct-common-redis/    # Redis配置
│
├── agriproduct-api/             # API接口定义
│   ├── agriproduct-api-user/        # 用户服务API
│   ├── agriproduct-api-product/     # 商品服务API
│   ├── agriproduct-api-order/       # 订单服务API
│   └── agriproduct-api-merchant/    # 商家服务API
│
├── agriproduct-service/         # 业务服务模块
│   ├── agriproduct-user/            # 用户服务（用户端+管理后台用户管理）
│   ├── agriproduct-product/         # 商品服务（商品管理、推荐、搜索）
│   ├── agriproduct-order/           # 订单服务（订单创建、管理、统计）
│   ├── agriproduct-merchant/        # 商家服务（商家管理、收益统计）
│   └── agriproduct-admin/           # 管理后台（审核、系统管理）
│
├── agriproduct-gateway/         # 网关服务（可选，用于微服务架构）
│
└── agriproduct-auth/            # 认证授权服务
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

### 阶段一：项目初始化
1. 创建Maven多模块项目结构
2. 配置Spring Boot基础依赖
3. 配置MySQL数据库连接
4. 配置MyBatis-Plus
5. 创建代码生成器配置

### 阶段二：基础模块开发
1. 公共模块开发
   - 统一响应结果封装
   - 统一异常处理
   - 分页封装
   - 工具类（ID生成、日期处理等）
2. 用户认证授权
   - JWT Token实现
   - 登录/注册接口
   - 权限拦截器

### 阶段三：核心业务模块
1. 商品服务
   - 商品CRUD
   - 商品搜索
   - 商品推荐算法
2. 订单服务
   - 购物车管理
   - 订单创建流程
   - 订单状态流转
3. 用户服务
   - 用户信息管理
   - 收货地址管理

### 阶段四：商家管理功能
1. 商家入驻审核
2. 商家商品管理
3. 商家订单管理
4. 收益统计分析

### 阶段五：管理后台功能
1. 商品审核
2. 用户管理
3. 轮播图配置
4. 数据统计看板

### 阶段六：前端开发
1. 商家端Vue3项目
2. 管理后台Vue3项目
3. 用户端小程序开发

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
agriproduct-user/
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

### 接口测试
- 使用Postman/Apifox进行API测试
- 单元测试覆盖率>80%

### 性能测试
- 并发下单测试
- 商品搜索响应时间<500ms
