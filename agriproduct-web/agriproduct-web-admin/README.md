# 农产品电商管理后台

基于 Vue3 + Vite + TypeScript + Element Plus 的管理后台前端项目

## 技术栈

- Vue 3.5.28
- TypeScript 5.9.3
- Vite 7.3.1
- Element Plus 2.13.2
- Pinia 3.0.4
- Vue Router 4.6.4
- Axios 1.13.5
- dayjs 1.11.19
- sass 1.97.3

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

访问地址：`http://localhost:3000`

## 构建

```bash
npm run build
```

## 功能模块

### 1. 登录模块
- 管理员登录
- JWT Token 认证

### 2. 仪表盘
- 数据统计卡片（用户、商家、商品、订单）
- 订单趋势图表
- 商品分类占比
- 最新入驻商家列表

### 3. 用户管理
- 用户列表展示
- 用户搜索（用户名、手机号）
- 启用/禁用用户
- 分页查询

### 4. 商家管理
- 商家列表展示
- 商家审核（通过/拒绝）
- 查看商家详情
- 营业执照预览

### 5. 商品审核
- 待审核商品列表
- 商品详情查看
- 审核通过/拒绝
- 拒绝原因记录

### 6. 轮播图管理
- 轮播图列表展示
- 新增轮播图
- 编辑轮播图
- 删除轮播图
- 图片预览
- 排序设置
- 启用/禁用

## 项目结构

```
agriproduct-web-admin/
├── public/                  # 静态资源
├── src/
│   ├── api/                # API 接口
│   │   ├── auth.ts         # 认证接口
│   │   ├── user.ts         # 用户管理接口
│   │   ├── merchant.ts     # 商家管理接口
│   │   ├── product.ts      # 商品审核接口
│   │   ├── banner.ts       # 轮播图接口
│   │   └── index.ts        # 接口导出
│   ├── assets/             # 资源文件
│   │   └── styles/         # 样式文件
│   │       ├── global.scss # 全局样式
│   │       ├── reset.scss  # 重置样式
│   │       └── variables.scss # 变量
│   ├── components/         # 公共组件
│   ├── router/             # 路由配置
│   ├── stores/             # Pinia 状态管理
│   │   ├── app.ts          # 应用状态
│   │   ├── user.ts         # 用户状态
│   │   └── index.ts        # store 导出
│   ├── types/              # TypeScript 类型定义
│   ├── utils/              # 工具函数
│   │   ├── auth.ts         # 认证工具
│   │   ├── format.ts       # 格式化工具
│   │   └── request.ts      # 请求封装
│   ├── views/              # 页面组件
│   │   ├── layout/         # 布局组件
│   │   ├── login/          # 登录页面
│   │   ├── dashboard/      # 仪表盘
│   │   ├── user/           # 用户管理
│   │   ├── merchant/       # 商家管理
│   │   ├── product/        # 商品审核
│   │   ├── banner/         # 轮播图管理
│   │   └── error/          # 错误页面
│   ├── App.vue             # 根组件
│   ├── main.ts             # 入口文件
│   ├── env.d.ts            # 环境变量类型
│   ├── auto-imports.d.ts   # 自动导入类型
│   └── components.d.ts     # 组件类型
├── .env                    # 环境变量
├── .gitignore              # Git 忽略配置
├── index.html              # HTML 模板
├── package.json            # 项目配置
├── tsconfig.json           # TypeScript 配置
├── vite.config.ts          # Vite 配置
└── README.md               # 项目说明
```

## 配置说明

### 环境变量

`.env` 文件中配置：

```env
VITE_API_BASE_URL=/api
```

### Vite 配置

- 端口：3000
- 代理：`/api` 代理到 `http://localhost:8080`

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88
