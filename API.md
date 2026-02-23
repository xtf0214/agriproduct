# 农产品交易平台 API 接口文档

## 目录

- [1. 统一认证模块](#1-统一认证模块)
- [2. 首页模块](#2-首页模块)
- [3. 商品管理模块](#3-商品管理模块)
- [4. 购物车管理模块](#4-购物车管理模块)
- [5. 订单管理模块](#5-订单管理模块)
- [6. 收货地址管理模块](#6-收货地址管理模块)
- [7. 商家管理模块](#7-商家管理模块)
- [8. 管理后台模块](#8-管理后台模块)

---

## 通用说明

### baseurl

baseurl: `http://localhost:8080`

### 响应格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 认证方式

需要登录的接口需在请求头中携带用户ID：

```
X-User-Id: {用户ID}
```

---

## 1. 统一认证模块

**基础路径**: `/api/auth`

### 1.1 统一登录

- **接口**: `POST /api/auth/login`
- **描述**: 统一登录接口，支持用户、商家、管理员登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| loginType | String | 否 | 登录类型：user-普通用户，merchant-商家，admin-管理员（不传则自动判断） |

**请求示例**:
```json
{
  "username": "user001",
  "password": "123456",
  "loginType": "user"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "xxx",
    "userId": 1,
    "username": "user001",
    "nickname": "用户A",
    "userType": "user"
  }
}
```

---

### 1.2 统一注册

- **接口**: `POST /api/auth/register`
- **描述**: 统一注册接口

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| nickname | String | 否 | 昵称 |
| phone | String | 是 | 手机号（格式：1[3-9]开头共11位） |
| registerType | String | 否 | 注册类型：user-普通用户（默认），merchant-商家 |

**请求示例**:
```json
{
  "username": "user001",
  "password": "123456",
  "nickname": "用户A",
  "phone": "13800138000",
  "registerType": "user"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

---

### 1.3 获取当前用户信息

- **接口**: `GET /api/auth/profile`
- **描述**: 获取当前登录用户的详细信息

**请求头**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "username": "user001",
    "nickname": "用户A",
    "phone": "13800138000",
    "avatar": "https://xxx.com/avatar.jpg"
  }
}
```

---

## 2. 首页模块

**基础路径**: `/api/home`

### 2.1 获取首页数据

- **接口**: `GET /api/home`
- **描述**: 获取首页所有数据（轮播图、分类、推荐商品）

**响应示例**:
```json
{
  "banners": [],
  "categories": [],
  "recommends": []
}
```

---

### 2.2 获取轮播图

- **接口**: `GET /api/home/banner`
- **描述**: 获取首页轮播图列表

**响应示例**:
```json
[
  {
    "id": 1,
    "title": "轮播图1",
    "imageUrl": "https://xxx.com/banner1.jpg",
    "linkUrl": "/product/1"
  }
]
```

---

### 2.3 获取分类列表

- **接口**: `GET /api/home/category`
- **描述**: 获取顶级分类列表

**响应示例**:
```json
[
  {
    "id": 1,
    "name": "水果",
    "icon": "https://xxx.com/icon.png"
  }
]
```

---

### 2.4 获取分类树

- **接口**: `GET /api/home/category/tree`
- **描述**: 获取完整的分类树结构

**响应示例**:
```json
[
  {
    "id": 1,
    "name": "水果",
    "children": [
      {
        "id": 11,
        "name": "苹果"
      }
    ]
  }
]
```

---

### 2.5 获取推荐商品

- **接口**: `GET /api/home/recommend`
- **描述**: 获取首页推荐商品

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量限制，默认10 |

**响应示例**:
```json
[
  {
    "id": 1,
    "name": "新鲜苹果",
    "price": 9.99,
    "imageUrl": "https://xxx.com/product1.jpg"
  }
]
```

---

## 3. 商品管理模块

**基础路径**: `/api/product`

### 3.1 分页查询商品列表

- **接口**: `GET /api/product/list`
- **描述**: 分页查询商品列表，支持多条件筛选

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| categoryId | Long | 否 | 分类ID |
| keyword | String | 否 | 搜索关键词 |
| priceSort | String | 否 | 价格排序：asc-升序，desc-降序 |
| salesSort | String | 否 | 销量排序：asc-升序，desc-降序 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "新鲜苹果",
        "price": 9.99,
        "stock": 100,
        "sales": 50,
        "imageUrl": "https://xxx.com/product1.jpg"
      }
    ],
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10
  }
}
```

---

### 3.2 获取商品详情

- **接口**: `GET /api/product/{id}`
- **描述**: 根据商品ID获取商品详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "新鲜苹果",
    "price": 9.99,
    "stock": 100,
    "sales": 50,
    "description": "产地直供，新鲜可口",
    "imageUrl": "https://xxx.com/product1.jpg",
    "images": ["https://xxx.com/1.jpg", "https://xxx.com/2.jpg"],
    "categoryId": 11,
    "merchantId": 1,
    "merchantName": "农家果园"
  }
}
```

---

### 3.3 搜索商品

- **接口**: `GET /api/product/search`
- **描述**: 根据关键词搜索商品

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 是 | 搜索关键词 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "新鲜苹果",
      "price": 9.99,
      "imageUrl": "https://xxx.com/product1.jpg"
    }
  ]
}
```

---

## 4. 购物车管理模块

**基础路径**: `/api/cart`

### 4.1 获取购物车列表

- **接口**: `GET /api/cart/list`
- **描述**: 获取当前用户的购物车列表

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "cartId": 1,
      "productId": 1,
      "productName": "新鲜苹果",
      "productImage": "https://xxx.com/product1.jpg",
      "price": 9.99,
      "quantity": 2,
      "subtotal": 19.98
    }
  ]
}
```

---

### 4.2 添加商品到购物车

- **接口**: `POST /api/cart`
- **描述**: 将商品添加到购物车

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品ID |
| quantity | Integer | 是 | 数量 |

**请求示例**:
```json
{
  "productId": 1,
  "quantity": 2
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4.3 更新购物车商品数量

- **接口**: `PUT /api/cart/{cartId}`
- **描述**: 更新购物车中商品的数量

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| cartId | Long | 是 | 购物车项ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| quantity | Integer | 是 | 新数量 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4.4 删除购物车项

- **接口**: `DELETE /api/cart/{cartId}`
- **描述**: 删除购物车中的商品

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| cartId | Long | 是 | 购物车项ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4.5 清空购物车

- **接口**: `DELETE /api/cart/clear`
- **描述**: 清空当前用户的购物车

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4.6 获取购物车商品数量

- **接口**: `GET /api/cart/count`
- **描述**: 获取当前用户购物车中商品的总数量

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

---

## 5. 订单管理模块

**基础路径**: `/api/order`

### 5.1 创建订单

- **接口**: `POST /api/order`
- **描述**: 创建订单（支持从购物车下单或直接购买）

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | Long | 是 | 收货地址ID |
| cartIds | List\<Long\> | 否 | 购物车商品ID列表（从购物车下单时使用） |
| productId | Long | 否 | 商品ID（直接购买时使用） |
| quantity | Integer | 否 | 数量（直接购买时使用） |
| remark | String | 否 | 订单备注 |

**请求示例（从购物车下单）**:
```json
{
  "addressId": 1,
  "cartIds": [1, 2, 3],
  "remark": "请尽快发货"
}
```

**请求示例（直接购买）**:
```json
{
  "addressId": 1,
  "productId": 1,
  "quantity": 2,
  "remark": "请尽快发货"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": "202602200001",
    "totalAmount": 99.99,
    "status": 0
  }
}
```

---

### 5.2 获取订单详情

- **接口**: `GET /api/order/{orderId}`
- **描述**: 根据订单ID获取订单详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": "202602200001",
    "totalAmount": 99.99,
    "status": 0,
    "statusText": "待付款",
    "createTime": "2026-02-20 10:00:00",
    "items": [
      {
        "productId": 1,
        "productName": "新鲜苹果",
        "productImage": "https://xxx.com/product1.jpg",
        "price": 9.99,
        "quantity": 10
      }
    ],
    "address": {
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "detailAddress": "XX省XX市XX区XX路XX号"
    }
  }
}
```

---

### 5.3 获取用户订单列表

- **接口**: `GET /api/order/list`
- **描述**: 分页获取当前用户的订单列表

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "orderId": "202602200001",
        "totalAmount": 99.99,
        "status": 0,
        "statusText": "待付款",
        "createTime": "2026-02-20 10:00:00"
      }
    ],
    "total": 10,
    "pages": 1,
    "current": 1,
    "size": 10
  }
}
```

---

### 5.4 取消订单

- **接口**: `PUT /api/order/{orderId}/cancel`
- **描述**: 用户取消订单

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 5.5 确认收货

- **接口**: `PUT /api/order/{orderId}/confirm`
- **描述**: 用户确认收货

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 6. 收货地址管理模块

**基础路径**: `/api/address`

### 6.1 获取地址列表

- **接口**: `GET /api/address/list`
- **描述**: 获取当前用户的收货地址列表

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "addressId": 1,
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "浙江省",
      "city": "杭州市",
      "district": "西湖区",
      "detailAddress": "XX路XX号",
      "isDefault": 1
    }
  ]
}
```

---

### 6.2 获取地址详情

- **接口**: `GET /api/address/{addressId}`
- **描述**: 根据ID获取收货地址详情

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | Long | 是 | 地址ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "addressId": 1,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "province": "浙江省",
    "city": "杭州市",
    "district": "西湖区",
    "detailAddress": "XX路XX号",
    "isDefault": 1
  }
}
```

---

### 6.3 创建收货地址

- **接口**: `POST /api/address`
- **描述**: 创建新的收货地址

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| receiverName | String | 是 | 收货人 |
| receiverPhone | String | 是 | 收货电话 |
| province | String | 否 | 省 |
| city | String | 否 | 市 |
| district | String | 否 | 区 |
| detailAddress | String | 是 | 详细地址 |
| isDefault | Integer | 否 | 是否默认：0-否，1-是 |

**请求示例**:
```json
{
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "province": "浙江省",
  "city": "杭州市",
  "district": "西湖区",
  "detailAddress": "XX路XX号",
  "isDefault": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

---

### 6.4 修改收货地址

- **接口**: `PUT /api/address/{addressId}`
- **描述**: 修改收货地址

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | Long | 是 | 地址ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| receiverName | String | 是 | 收货人 |
| receiverPhone | String | 是 | 收货电话 |
| province | String | 否 | 省 |
| city | String | 否 | 市 |
| district | String | 否 | 区 |
| detailAddress | String | 是 | 详细地址 |
| isDefault | Integer | 否 | 是否默认：0-否，1-是 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 6.5 删除收货地址

- **接口**: `DELETE /api/address/{addressId}`
- **描述**: 删除收货地址

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | Long | 是 | 地址ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 6.6 设置默认地址

- **接口**: `PUT /api/address/{addressId}/default`
- **描述**: 将指定地址设置为默认地址

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | Long | 是 | 地址ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 6.7 获取默认地址

- **接口**: `GET /api/address/default`
- **描述**: 获取当前用户的默认收货地址

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "addressId": 1,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "province": "浙江省",
    "city": "杭州市",
    "district": "西湖区",
    "detailAddress": "XX路XX号",
    "isDefault": 1
  }
}
```

---

## 7. 商家管理模块

**基础路径**: `/api/merchant`

### 7.1 商家入驻申请

- **接口**: `POST /api/merchant/apply`
- **描述**: 用户申请成为商家

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| shopName | String | 是 | 店铺名称 |
| shopDesc | String | 否 | 店铺描述 |
| businessLicense | String | 是 | 营业执照图片URL |
| contactPhone | String | 是 | 联系电话 |
| contactName | String | 是 | 联系人姓名 |

**请求示例**:
```json
{
  "shopName": "农家果园",
  "shopDesc": "产地直供，新鲜水果",
  "businessLicense": "https://xxx.com/license.jpg",
  "contactPhone": "13800138000",
  "contactName": "张三"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

---

### 7.2 获取商家信息

- **接口**: `GET /api/merchant/{id}`
- **描述**: 根据ID获取商家信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商家ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "shopName": "农家果园",
    "shopDesc": "产地直供，新鲜水果",
    "contactPhone": "13800138000",
    "status": 1
  }
}
```

---

### 7.3 获取当前用户的商家信息

- **接口**: `GET /api/merchant/info`
- **描述**: 获取当前登录用户的商家信息

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "shopName": "农家果园",
    "shopDesc": "产地直供，新鲜水果",
    "contactPhone": "13800138000",
    "status": 1
  }
}
```

---

### 7.4 更新商家信息

- **接口**: `PUT /api/merchant/{id}`
- **描述**: 更新商家信息

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商家ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| shopName | String | 是 | 店铺名称 |
| shopDesc | String | 否 | 店铺描述 |
| businessLicense | String | 是 | 营业执照图片URL |
| contactPhone | String | 是 | 联系电话 |
| contactName | String | 是 | 联系人姓名 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 7.5 获取商家订单列表

- **接口**: `GET /api/merchant/orders`
- **描述**: 分页获取商家的订单列表

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "orderId": "202602200001",
        "totalAmount": 99.99,
        "status": 1,
        "statusText": "待发货",
        "createTime": "2026-02-20 10:00:00"
      }
    ],
    "total": 10,
    "pages": 1,
    "current": 1,
    "size": 10
  }
}
```

---

### 7.6 订单发货

- **接口**: `PUT /api/merchant/order/{orderId}/ship`
- **描述**: 商家对订单进行发货操作

**请求头**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| X-User-Id | Long | 是 | 用户ID |

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 8. 管理后台模块

**基础路径**: `/api/admin`

### 8.1 用户管理

#### 8.1.1 获取用户列表

- **接口**: `GET /api/admin/user/list`
- **描述**: 分页获取用户列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "userId": 1,
        "username": "user001",
        "nickname": "用户A",
        "phone": "13800138000",
        "status": 1
      }
    ],
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10
  }
}
```

#### 8.1.2 更新用户状态

- **接口**: `PUT /api/admin/user/{id}/status`
- **描述**: 更新用户状态（启用/禁用）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 状态：0-禁用，1-正常 |

**请求示例**:
```json
{
  "status": 0
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 8.2 商家管理

#### 8.2.1 获取商家列表

- **接口**: `GET /api/admin/merchant/list`
- **描述**: 分页获取商家列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "shopName": "农家果园",
        "status": 1,
        "createTime": "2026-02-20 10:00:00"
      }
    ],
    "total": 50,
    "pages": 5,
    "current": 1,
    "size": 10
  }
}
```

#### 8.2.2 审核商家入驻

- **接口**: `PUT /api/admin/merchant/{id}/audit`
- **描述**: 审核商家入驻申请

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商家ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| auditStatus | Integer | 是 | 审核状态：1-通过，2-拒绝 |
| auditRemark | String | 否 | 审核备注 |

**请求示例**:
```json
{
  "auditStatus": 1,
  "auditRemark": "审核通过"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 8.3 商品审核

#### 8.3.1 获取待审核商品列表

- **接口**: `GET /api/admin/product/audit/list`
- **描述**: 分页获取待审核商品列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "新鲜苹果",
        "merchantName": "农家果园",
        "status": 0
      }
    ],
    "total": 20,
    "pages": 2,
    "current": 1,
    "size": 10
  }
}
```

#### 8.3.2 审核商品

- **接口**: `PUT /api/admin/product/{id}/audit`
- **描述**: 审核商品（通过/拒绝）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| auditStatus | Integer | 是 | 审核状态：1-通过，2-拒绝 |
| auditReason | String | 否 | 审核原因 |

**请求示例**:
```json
{
  "auditStatus": 1,
  "auditReason": "商品信息完整，审核通过"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

#### 8.4.2 新增轮播图

- **接口**: `POST /api/admin/banner`
- **描述**: 新增轮播图

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 否 | 轮播图标题 |
| image | String | 是 | 图片URL |
| linkType | Integer | 否 | 链接类型：1-无，2-商品，3-分类 |
| linkId | Long | 否 | 关联ID |
| sortOrder | Integer | 否 | 排序值 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 6
}
```

#### 8.4.3 修改轮播图

- **接口**: `PUT /api/admin/banner/{id}`
- **描述**: 修改轮播图

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 轮播图ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 否 | 轮播图标题 |
| image | String | 是 | 图片URL |
| linkType | Integer | 否 | 链接类型：1-无，2-商品，3-分类 |
| linkId | Long | 否 | 关联ID |
| sortOrder | Integer | 否 | 排序值 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

#### 8.4.4 删除轮播图

- **接口**: `DELETE /api/admin/banner/{id}`
- **描述**: 删除轮播图

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 轮播图ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 8.4 轮播图管理

#### 8.4.1 获取轮播图列表

- **接口**: `GET /api/admin/banner/list`
- **描述**: 分页获取轮播图列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "轮播图1",
        "imageUrl": "https://xxx.com/banner1.jpg",
        "linkUrl": "/product/1",
        "status": 1
      }
    ],
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 10
  }
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权/未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

*文档生成时间: 2026-02-20*
