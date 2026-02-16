-- =============================================
-- 农产品电商平台 - 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `agriproduct` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agriproduct`;

-- =============================================
-- 用户模块
-- =============================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(500) COMMENT '头像',
    `password` VARCHAR(200) COMMENT '密码',
    `user_type` TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型:1-普通用户,2-商家,3-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 收货地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货电话',
    `province` VARCHAR(50) COMMENT '省',
    `city` VARCHAR(50) COMMENT '市',
    `district` VARCHAR(50) COMMENT '区',
    `detail_address` VARCHAR(200) COMMENT '详细地址',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认:0-否,1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- =============================================
-- 商家模块
-- =============================================

-- 商家表
DROP TABLE IF EXISTS `mer_merchant`;
CREATE TABLE `mer_merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商家ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `shop_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `shop_desc` VARCHAR(500) COMMENT '店铺描述',
    `shop_logo` VARCHAR(500) COMMENT '店铺logo',
    `business_license` VARCHAR(500) COMMENT '营业执照',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
    `audit_reason` VARCHAR(500) COMMENT '审核备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家表';

-- =============================================
-- 商品模块
-- =============================================

-- 商品分类表
DROP TABLE IF EXISTS `prod_category`;
CREATE TABLE `prod_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID',
    `icon` VARCHAR(500) COMMENT '分类图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS `prod_product`;
CREATE TABLE `prod_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `category_id` BIGINT COMMENT '分类ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(200) COMMENT '副标题',
    `main_image` VARCHAR(500) COMMENT '主图',
    `images` TEXT COMMENT '商品图片JSON',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) COMMENT '原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `detail` TEXT COMMENT '商品详情HTML',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-下架,1-上架,2-审核中',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
    `audit_reason` VARCHAR(500) COMMENT '审核备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 轮播图表
DROP TABLE IF EXISTS `content_banner`;
CREATE TABLE `content_banner` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    `title` VARCHAR(100) COMMENT '标题',
    `image` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_type` TINYINT NOT NULL DEFAULT 1 COMMENT '链接类型:1-无,2-商品,3-分类',
    `link_id` BIGINT COMMENT '关联ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- =============================================
-- 购物车模块
-- =============================================

-- 购物车表
DROP TABLE IF EXISTS `cart_shopping`;
CREATE TABLE `cart_shopping` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- =============================================
-- 订单模块
-- =============================================

-- 订单表
DROP TABLE IF EXISTS `order_order`;
CREATE TABLE `order_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-待付款,2-待发货,3-待收货,4-已完成,5-已取消',
    `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态:0-未支付,1-已支付',
    `pay_time` DATETIME COMMENT '支付时间',
    `ship_time` DATETIME COMMENT '发货时间',
    `finish_time` DATETIME COMMENT '完成时间',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货电话',
    `receiver_address` VARCHAR(300) NOT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单商品明细表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(500) COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品明细表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `nickname`, `password`, `user_type`, `status`) VALUES
('admin', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 1);

-- 插入测试用户 (密码: 123456)
INSERT INTO `sys_user` (`username`, `nickname`, `phone`, `password`, `user_type`, `status`) VALUES
('testuser', '测试用户', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
('testmerchant', '测试商家', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1);

-- 插入商品分类
INSERT INTO `prod_category` (`name`, `parent_id`, `sort_order`) VALUES
('新鲜蔬菜', 0, 1),
('新鲜水果', 0, 2),
('肉禽蛋品', 0, 3),
('海鲜水产', 0, 4),
('粮油调味', 0, 5);

-- 插入测试商家
INSERT INTO `mer_merchant` (`user_id`, `shop_name`, `shop_desc`, `status`) VALUES
(3, '绿色农场', '提供优质新鲜农产品', 1);

-- 插入测试商品
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `price`, `original_price`, `stock`, `status`, `audit_status`) VALUES
(1, 1, '有机菠菜', '新鲜有机菠菜 500g', 'https://example.com/spinach.jpg', 8.00, 10.00, 100, 1, 1),
(1, 2, '红富士苹果', '新鲜脆甜 红富士苹果 5斤', 'https://example.com/apple.jpg', 29.90, 35.00, 50, 1, 1),
(1, 3, '土鸡蛋', '散养土鸡蛋 30枚', 'https://example.com/egg.jpg', 35.00, 40.00, 80, 1, 1);
