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
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_name` VARCHAR(50) COMMENT '联系人',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
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

-- 插入管理员账号 (密码: admin123)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `phone`, `password`, `user_type`, `status`) VALUES
(1, 'admin', '超级管理员', '13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 1),
(2, 'admin02', '运营管理员', '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 1);

-- 插入普通用户 (密码: 123456)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `phone`, `avatar`, `password`, `user_type`, `status`) VALUES
(3, 'user01', '张三', '13800138001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(4, 'user02', '李四', '13800138002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(5, 'user03', '王五', '13800138003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(6, 'user04', '赵六', '13800138004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(7, 'user05', '孙七', '13800138005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 0),
(8, 'user06', '周八', '13800138006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(9, 'user07', '吴九', '13800138007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1),
(10, 'user08', '郑十', '13800138008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1);

-- 插入商家账号 (密码: 123456)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `phone`, `avatar`, `password`, `user_type`, `status`) VALUES
(11, 'merchant01', '绿色农场', '13900139001', 'https://api.dicebear.com/7.x/initials/svg?seed=GF', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1),
(12, 'merchant02', '田园果业', '13900139002', 'https://api.dicebear.com/7.x/initials/svg?seed=TY', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1),
(13, 'merchant03', '深海渔港', '13900139003', 'https://api.dicebear.com/7.x/initials/svg?seed=SH', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1),
(14, 'merchant04', '山里禽蛋', '13900139004', 'https://api.dicebear.com/7.x/initials/svg?seed=SL', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1),
(15, 'merchant05', '粮油世家', '13900139005', 'https://api.dicebear.com/7.x/initials/svg?seed=LY', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 1);

-- 插入收货地址
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(3, '张三', '13800138001', '北京市', '北京市', '朝阳区', '朝阳区建国路88号', 1),
(3, '张三', '13800138001', '北京市', '北京市', '海淀区', '海淀区中关村大街1号', 0),
(4, '李四', '13800138002', '上海市', '上海市', '浦东新区', '浦东新区世纪大道100号', 1),
(4, '李四', '13800138002', '上海市', '上海市', '黄浦区', '黄浦区南京东路200号', 0),
(5, '王五', '13800138003', '广东省', '广州市', '天河区', '天河区天河路123号', 1),
(6, '赵六', '13800138004', '广东省', '深圳市', '南山区', '南山区科技园南区', 1),
(7, '孙七', '13800138005', '浙江省', '杭州市', '西湖区', '西湖区文一西路456号', 1),
(8, '周八', '13800138006', '江苏省', '南京市', '鼓楼区', '鼓楼区中山路789号', 1),
(9, '吴九', '13800138007', '四川省', '成都市', '武侯区', '武侯区天府大道1000号', 1),
(10, '郑十', '13800138008', '湖北省', '武汉市', '江汉区', '江汉区解放大道666号', 1);

-- 插入商品分类（一级分类）
INSERT INTO `prod_category` (`id`, `name`, `parent_id`, `icon`, `sort_order`, `status`) VALUES
(1, '新鲜蔬菜', 0, 'https://img.icons8.com/color/96/vegetable.png', 1, 1),
(2, '新鲜水果', 0, 'https://img.icons8.com/color/96/apple.png', 2, 1),
(3, '肉禽蛋品', 0, 'https://img.icons8.com/color/96/meat.png', 3, 1),
(4, '海鲜水产', 0, 'https://img.icons8.com/color/96/fish.png', 4, 1),
(5, '粮油调味', 0, 'https://img.icons8.com/color/96/grain.png', 5, 1),
(6, '乳品烘焙', 0, 'https://img.icons8.com/color/96/milk.png', 6, 1),
(7, '休闲食品', 0, 'https://img.icons8.com/color/96/snack.png', 7, 1);

-- 插入商品分类（二级分类 - 蔬菜）
INSERT INTO `prod_category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('叶菜类', 1, 1, 1),
('根茎类', 1, 2, 1),
('茄果类', 1, 3, 1),
('瓜类', 1, 4, 1),
('菌菇类', 1, 5, 1);

-- 插入商品分类（二级分类 - 水果）
INSERT INTO `prod_category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('苹果', 2, 1, 1),
('柑橘橙', 2, 2, 1),
('热带水果', 2, 3, 1),
('浆果类', 2, 4, 1),
('瓜果类', 2, 5, 1);

-- 插入商品分类（二级分类 - 肉禽蛋）
INSERT INTO `prod_category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('猪肉', 3, 1, 1),
('牛肉', 3, 2, 1),
('羊肉', 3, 3, 1),
('禽肉', 3, 4, 1),
('蛋类', 3, 5, 1);

-- 插入商家信息
INSERT INTO `mer_merchant` (`id`, `user_id`, `shop_name`, `shop_desc`, `shop_logo`, `business_license`, `contact_phone`, `contact_name`, `status`, `audit_remark`) VALUES
(1, 11, '绿色农场', '专注有机农产品种植，为您提供最新鲜的蔬菜水果', 'https://example.com/logo1.png', 'license001.jpg', '13900139001', '王经理', 1, NULL),
(2, 12, '田园果业', '精选全国各地优质水果，产地直供', 'https://example.com/logo2.png', 'license002.jpg', '13900139002', '李经理', 1, NULL),
(3, 13, '深海渔港', '当日新鲜海鲜，直采直送', 'https://example.com/logo3.png', 'license003.jpg', '13900139003', '陈船长', 0, NULL),
(4, 14, '山里禽蛋', '散养土鸡土鸭，鸡蛋鸭蛋', 'https://example.com/logo4.png', 'license004.jpg', '13900139004', '张大爷', 2, '营业执照不清晰，请重新上传'),
(5, 15, '粮油世家', '优质粮油，调味专家', 'https://example.com/logo5.png', 'license005.jpg', '13900139005', '刘掌柜', 1, NULL);

-- 插入商品（绿色农场）
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `price`, `original_price`, `stock`, `sales`, `status`, `audit_status`, `audit_reason`) VALUES
(1, 8, '有机菠菜', '新鲜有机菠菜 500g 当日采摘', 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400', '["https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400"]', 8.00, 10.00, 200, 56, 1, 1, NULL),
(1, 9, '农家土豆', '黄心土豆 1000g 粉糯香甜', 'https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400', '["https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400"]', 6.50, 8.00, 150, 89, 1, 1, NULL),
(1, 10, '新鲜番茄', '大红番茄 500g 酸甜可口', 'https://images.unsplash.com/photo-1546470427-227c7369a9b8?w=400', '["https://images.unsplash.com/photo-1546470427-227c7369a9b8?w=400"]', 7.00, 9.00, 180, 123, 1, 1, NULL),
(1, 11, '黄瓜', '新鲜刺黄瓜 500g 脆嫩爽口', 'https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400', '["https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400"]', 5.50, 7.00, 200, 67, 1, 1, NULL),
(1, 12, '香菇', '新鲜香菇 250g 肉厚味美', 'https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400', '["https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400"]', 12.00, 15.00, 80, 45, 1, 1, NULL),
(1, 13, '红富士苹果', '陕西红富士苹果 5斤装 脆甜多汁', 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400', '["https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400"]', 29.90, 38.00, 100, 234, 1, 1, NULL),
(1, 14, '赣南脐橙', '江西赣南脐橙 5斤装 皮薄肉厚', 'https://images.unsplash.com/photo-1547514701-42782101795e?w=400', '["https://images.unsplash.com/photo-1547514701-42782101795e?w=400"]', 35.00, 45.00, 80, 167, 1, 1, NULL),
(1, 18, '土鸡蛋', '散养土鸡蛋 30枚 营养健康', 'https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?w=400', '["https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?w=400"]', 35.00, 42.00, 150, 312, 1, 1, NULL);

-- 插入商品（田园果业）
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `price`, `original_price`, `stock`, `sales`, `status`, `audit_status`, `audit_reason`) VALUES
(2, 13, '红富士苹果', '烟台红富士 5斤装 特级果', 'https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400', '["https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400"]', 32.00, 42.00, 200, 456, 1, 1, NULL),
(2, 14, '砂糖橘', '广西砂糖橘 3斤装 甜蜜多汁', 'https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400', '["https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400"]', 25.00, 30.00, 150, 289, 1, 1, NULL),
(2, 15, '芒果', '海南大台农芒果 3斤装 香甜可口', 'https://images.unsplash.com/photo-1553279768-865429fa0078?w=400', '["https://images.unsplash.com/photo-1553279768-865429fa0078?w=400"]', 28.00, 35.00, 100, 178, 1, 1, NULL),
(2, 16, '蓝莓', '智利进口蓝莓 125g 新鲜直达', 'https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400', '["https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400"]', 18.00, 22.00, 60, 145, 1, 1, NULL),
(2, 17, '哈密瓜', '新疆西州蜜哈密瓜 1个 网纹香甜', 'https://images.unsplash.com/photo-1595366392896-5bb28e3a0062?w=400', '["https://images.unsplash.com/photo-1595366392896-5bb28e3a0062?w=400"]', 15.00, 20.00, 80, 98, 1, 1, NULL),
(2, 8, '生菜', '新鲜球生菜 300g 沙拉专用', 'https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400', '["https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400"]', 4.50, 6.00, 120, 87, 1, 1, NULL);

-- 插入商品（深海渔港 - 待审核）
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `price`, `original_price`, `stock`, `sales`, `status`, `audit_status`, `audit_reason`) VALUES
(3, 4, '基围虾', '活冻基围虾 500g 鲜活速冻', 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400', '["https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400"]', 45.00, 55.00, 50, 0, 2, 0, NULL),
(3, 4, '大黄鱼', '新鲜大黄鱼 500g 肉质鲜美', 'https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=400', '["https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=400"]', 38.00, 48.00, 40, 0, 2, 0, NULL),
(3, 4, '扇贝', '鲜活扇贝 1kg 肥美多汁', 'https://images.unsplash.com/photo-1618221835733-1b989ca30038?w=400', '["https://images.unsplash.com/photo-1618221835733-1b989ca30038?w=400"]', 68.00, 85.00, 30, 0, 2, 0, NULL);

-- 插入商品（山里禽蛋 - 审核拒绝）
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `price`, `original_price`, `stock`, `sales`, `status`, `audit_status`, `audit_reason`) VALUES
(4, 18, '咸鸭蛋', '高邮咸鸭蛋 10枚 流油起沙', 'https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400', '["https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400"]', 25.00, 30.00, 100, 0, 0, 2, '产品图片与描述不符，请重新上传'),
(4, 20, '土鸭蛋', '散养土鸭蛋 15枚 营养丰富', 'https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400', '["https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400"]', 30.00, 38.00, 80, 0, 0, 2, '缺少生产日期标识');

-- 插入商品（粮油世家）
INSERT INTO `prod_product` (`merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `price`, `original_price`, `stock`, `sales`, `status`, `audit_status`, `audit_reason`) VALUES
(5, 5, '东北大米', '五常大米 5kg 香软可口', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400', '["https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400"]', 65.00, 78.00, 200, 445, 1, 1, NULL),
(5, 5, '花生油', '古法花生油 5L 香味浓郁', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400', '["https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400"]', 128.00, 158.00, 80, 234, 1, 1, NULL),
(5, 5, '酱油', '特级生抽酱油 500ml 鲜味十足', 'https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400', '["https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400"]', 12.00, 15.00, 300, 567, 1, 1, NULL),
(5, 5, '山西老陈醋', '山西老陈醋 500ml 酸香醇厚', 'https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400', '["https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400"]', 15.00, 18.00, 150, 321, 1, 1, NULL),
(5, 6, '纯牛奶', '纯牛奶 250ml*24盒 营养健康', 'https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400', '["https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400"]', 58.00, 68.00, 200, 678, 1, 1, NULL);

-- 插入轮播图
INSERT INTO `content_banner` (`id`, `title`, `image`, `link_type`, `link_id`, `sort_order`, `status`) VALUES
(1, '新鲜蔬菜限时优惠', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=1200', 2, 1, 1, 1),
(2, '时令水果热卖中', 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=1200', 3, 2, 2, 1),
(3, '海鲜水产新品上市', 'https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=1200', 1, NULL, 3, 1),
(4, '绿色农场专场活动', 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=1200', 2, 6, 4, 1),
(5, '新人专享优惠', 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=1200', 1, NULL, 5, 1);

-- 插入购物车数据
INSERT INTO `cart_shopping` (`user_id`, `product_id`, `quantity`) VALUES
(3, 1, 2), (3, 2, 1), (3, 6, 3),
(4, 9, 1), (4, 10, 2),
(5, 13, 5),
(6, 15, 1), (6, 16, 2),
(8, 1, 1), (8, 7, 3),
(9, 18, 2), (9, 20, 1);

-- 插入订单数据
INSERT INTO `order_order` (`id`, `order_no`, `user_id`, `merchant_id`, `total_amount`, `pay_amount`, `status`, `pay_status`, `pay_time`, `receiver_name`, `receiver_phone`, `receiver_address`, `remark`) VALUES
(1, 'ORD20240101001', 3, 1, 24.00, 24.00, 4, 1, '2024-01-01 10:30:00', '张三', '13800138001', '北京市朝阳区建国路88号', '尽快发货'),
(2, 'ORD20240101002', 3, 2, 50.00, 50.00, 4, 1, '2024-01-02 14:20:00', '张三', '13800138001', '北京市海淀区中关村大街1号', NULL),
(3, 'ORD20240102001', 4, 1, 15.00, 15.00, 4, 1, '2024-01-03 09:15:00', '李四', '13800138002', '上海市浦东新区世纪大道100号', NULL),
(4, 'ORD20240103001', 5, 2, 28.00, 28.00, 3, 1, '2024-01-04 16:45:00', '王五', '13800138003', '广东省广州市天河区天河路123号', '小心轻放'),
(5, 'ORD20240104001', 6, 1, 8.00, 8.00, 2, 1, '2024-01-05 11:30:00', '赵六', '13800138004', '广东省深圳市南山区科技园南区', NULL),
(6, 'ORD20240105001', 8, 5, 65.00, 65.00, 4, 1, '2024-01-06 10:00:00', '周八', '13800138006', '江苏省南京市鼓楼区中山路789号', NULL),
(7, 'ORD20240106001', 3, 2, 18.00, 18.00, 1, 0, NULL, '张三', '13800138001', '北京市朝阳区建国路88号', NULL),
(8, 'ORD20240107001', 9, 1, 16.00, 16.00, 5, 0, NULL, '吴九', '13800138007', '四川省成都市武侯区天府大道1000号', NULL);

-- 插入订单商品明细
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `total_amount`) VALUES
(1, 1, '有机菠菜', 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400', 8.00, 3, 24.00),
(2, 9, '生菜', 'https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400', 4.50, 2, 9.00),
(2, 13, '红富士苹果', 'https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400', 32.00, 1, 32.00),
(3, 2, '农家土豆', 'https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400', 6.50, 1, 6.50),
(3, 10, '黄瓜', 'https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400', 5.50, 1, 5.50),
(3, 11, '香菇', 'https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400', 12.00, 1, 12.00),
(4, 15, '芒果', 'https://images.unsplash.com/photo-1553279768-865429fa0078?w=400', 28.00, 1, 28.00),
(5, 1, '有机菠菜', 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400', 8.00, 1, 8.00),
(6, 21, '东北大米', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400', 65.00, 1, 65.00),
(7, 16, '蓝莓', 'https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400', 18.00, 1, 18.00),
(8, 7, '红富士苹果', 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400', 29.90, 1, 29.90);
