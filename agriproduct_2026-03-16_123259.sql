-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: agriproduct
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart_shopping`
--

DROP TABLE IF EXISTS `cart_shopping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_shopping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_shopping`
--

/*!40000 ALTER TABLE `cart_shopping` DISABLE KEYS */;
INSERT INTO `cart_shopping` VALUES (4,4,9,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,4,10,2,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(6,5,13,5,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(7,6,15,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(8,6,16,2,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(9,8,1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(10,8,7,3,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(11,9,18,2,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(12,9,20,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(16,3,24,1,'2026-03-13 15:31:16','2026-03-13 15:31:16');
/*!40000 ALTER TABLE `cart_shopping` ENABLE KEYS */;

--
-- Table structure for table `content_banner`
--

DROP TABLE IF EXISTS `content_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `content_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `image` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_type` tinyint NOT NULL DEFAULT '1' COMMENT '链接类型:1-无,2-商品,3-分类',
  `link_id` bigint DEFAULT NULL COMMENT '关联ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_banner`
--

/*!40000 ALTER TABLE `content_banner` DISABLE KEYS */;
INSERT INTO `content_banner` VALUES (1,'新鲜蔬菜限时优惠','https://images.unsplash.com/photo-1540420773420-3366772f4999?w=1200',2,1,1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(2,'时令水果热卖中','https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=1200',3,2,2,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,'海鲜水产新品上市','https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=1200',1,NULL,3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(4,'绿色农场专场活动','https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=1200',2,6,4,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,'新人专享优惠','https://images.unsplash.com/photo-1542838132-92c53300491e?w=1200',1,NULL,5,1,'2026-02-17 12:14:35','2026-02-17 12:14:35');
/*!40000 ALTER TABLE `content_banner` ENABLE KEYS */;

--
-- Table structure for table `mer_merchant`
--

DROP TABLE IF EXISTS `mer_merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mer_merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `shop_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店铺名称',
  `shop_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '店铺描述',
  `shop_logo` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '店铺logo',
  `business_license` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '营业执照',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mer_merchant`
--

/*!40000 ALTER TABLE `mer_merchant` DISABLE KEYS */;
INSERT INTO `mer_merchant` VALUES (1,5,'绿色农场','专注有机农产品种植，为您提供最新鲜的蔬菜水果','https://example.com/logo1.png','license001.jpg','13900139001','王经理',1,NULL,'2026-02-17 12:14:35','2026-03-13 23:42:18'),(2,6,'田园果业','精选全国各地优质水果，产地直供','https://example.com/logo2.png','license002.jpg','13900139002','李经理',1,NULL,'2026-02-17 12:14:35','2026-03-13 23:42:18'),(3,7,'深海渔港','当日新鲜海鲜，直采直送','https://example.com/logo3.png','license003.jpg','13900139003','陈船长',1,'','2026-02-17 12:14:35','2026-03-13 23:42:18'),(4,8,'山里禽蛋','散养土鸡土鸭，鸡蛋鸭蛋','https://example.com/logo4.png','license004.jpg','13900139004','张大爷',2,'','2026-02-17 12:14:35','2026-03-14 01:41:59'),(5,9,'粮油世家','优质粮油，调味专家','https://example.com/logo5.png','license005.jpg','13900139005','刘掌柜',1,NULL,'2026-02-17 12:14:35','2026-03-13 23:42:18');
/*!40000 ALTER TABLE `mer_merchant` ENABLE KEYS */;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '小计',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,1,'有机菠菜','https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400',8.00,3,24.00,'2026-02-17 12:14:35'),(2,2,9,'生菜','https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400',4.50,2,9.00,'2026-02-17 12:14:35'),(3,2,13,'红富士苹果','https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400',32.00,1,32.00,'2026-02-17 12:14:35'),(4,3,2,'农家土豆','https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400',6.50,1,6.50,'2026-02-17 12:14:35'),(5,3,10,'黄瓜','https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400',5.50,1,5.50,'2026-02-17 12:14:35'),(6,3,11,'香菇','https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400',12.00,1,12.00,'2026-02-17 12:14:35'),(7,4,15,'芒果','https://images.unsplash.com/photo-1553279768-865429fa0078?w=400',28.00,1,28.00,'2026-02-17 12:14:35'),(8,5,1,'有机菠菜','https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400',8.00,1,8.00,'2026-02-17 12:14:35'),(9,6,21,'东北大米','https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400',65.00,1,65.00,'2026-02-17 12:14:35'),(10,7,16,'蓝莓','https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400',18.00,1,18.00,'2026-02-17 12:14:35'),(11,8,7,'红富士苹果','https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400',29.90,1,29.90,'2026-02-17 12:14:35'),(12,9,1,'有机菠菜','https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400',8.00,3,24.00,'2026-03-02 09:41:51'),(13,9,2,'农家土豆','https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400',6.50,1,6.50,'2026-03-02 09:41:51'),(14,9,6,'红富士苹果','https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400',29.90,4,119.60,'2026-03-02 09:41:51'),(15,10,24,'纯牛奶','https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400',58.00,1,58.00,'2026-03-13 10:04:25'),(16,11,24,'纯牛奶','https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400',58.00,1,58.00,'2026-03-13 14:26:50'),(17,12,24,'纯牛奶','https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400',58.00,1,58.00,'2026-03-13 15:19:32');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;

--
-- Table structure for table `order_order`
--

DROP TABLE IF EXISTS `order_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:1-待付款,2-待发货,3-待收货,4-已完成,5-已取消',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-已支付',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货电话',
  `receiver_address` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货地址',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_order`
--

/*!40000 ALTER TABLE `order_order` DISABLE KEYS */;
INSERT INTO `order_order` VALUES (1,'ORD20240101001',3,1,24.00,24.00,4,1,'2024-01-01 10:30:00',NULL,NULL,'张三','13800138001','北京市朝阳区建国路88号','尽快发货','2026-02-17 12:14:35','2026-02-17 12:14:35'),(2,'ORD20240101002',3,2,50.00,50.00,4,1,'2024-01-02 14:20:00',NULL,NULL,'张三','13800138001','北京市海淀区中关村大街1号',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,'ORD20240102001',4,1,15.00,15.00,4,1,'2024-01-03 09:15:00',NULL,NULL,'李四','13800138002','上海市浦东新区世纪大道100号',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(4,'ORD20240103001',5,2,28.00,28.00,3,1,'2024-01-04 16:45:00',NULL,NULL,'王五','13800138003','广东省广州市天河区天河路123号','小心轻放','2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,'ORD20240104001',6,1,8.00,8.00,2,1,'2024-01-05 11:30:00',NULL,NULL,'赵六','13800138004','广东省深圳市南山区科技园南区',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(6,'ORD20240105001',8,5,65.00,65.00,4,1,'2024-01-06 10:00:00',NULL,NULL,'周八','13800138006','江苏省南京市鼓楼区中山路789号',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(7,'ORD20240106001',3,2,18.00,18.00,1,0,NULL,NULL,NULL,'张三','13800138001','北京市朝阳区建国路88号',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(8,'ORD20240107001',9,1,16.00,16.00,5,0,NULL,NULL,NULL,'吴九','13800138007','四川省成都市武侯区天府大道1000号',NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(9,'ORD17724157104372028284615160107008',3,1,150.10,150.10,3,1,'2026-03-02 10:48:03','2026-03-02 11:51:02',NULL,'张三','13800138001','北京市北京市朝阳区朝阳区建国路88号','','2026-03-02 09:41:51','2026-03-02 09:41:51'),(10,'ORD17733674644912032276560521859072',3,5,58.00,58.00,1,0,NULL,NULL,NULL,'张三','13800138001','北京市北京市朝阳区朝阳区建国路88号','','2026-03-13 10:04:25','2026-03-13 10:04:25'),(11,'ORD17733832097032032342600463290368',3,5,58.00,58.00,2,1,'2026-03-13 14:26:59',NULL,NULL,'张三','13800138001','北京市北京市朝阳区朝阳区建国路88号','','2026-03-13 14:26:50','2026-03-13 14:26:50'),(12,'ORD17733863723832032355865813716992',3,5,58.00,58.00,2,1,'2026-03-13 17:58:24',NULL,NULL,'张三','13800138001','北京市北京市朝阳区朝阳区建国路88号','','2026-03-13 15:19:32','2026-03-13 15:19:32');
/*!40000 ALTER TABLE `order_order` ENABLE KEYS */;

--
-- Table structure for table `prod_category`
--

DROP TABLE IF EXISTS `prod_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prod_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `icon` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prod_category`
--

/*!40000 ALTER TABLE `prod_category` DISABLE KEYS */;
INSERT INTO `prod_category` VALUES (1,'新鲜蔬菜',0,'https://img.icons8.com/?size=96&id=cpa3RyNsYJkU&format=png',1,1,'2026-02-17 12:14:35','2026-03-13 15:13:22'),(2,'新鲜水果',0,'https://img.icons8.com/color/96/apple.png',2,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,'肉禽蛋品',0,'https://img.icons8.com/?size=96&id=iErixLlt7v9F&format=png',3,1,'2026-02-17 12:14:35','2026-03-13 15:13:23'),(4,'海鲜水产',0,'https://img.icons8.com/color/96/fish.png',4,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,'粮油调味',0,'https://img.icons8.com/color/96/olive-oil-bottle.jpg',5,1,'2026-02-17 12:14:35','2026-03-13 15:13:23'),(6,'乳品烘焙',0,'https://img.icons8.com/color/96/milk.png',6,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(7,'休闲食品',0,'https://img.icons8.com/color/96/potato-chips.jpg',7,1,'2026-02-17 12:14:35','2026-03-13 15:13:23'),(8,'叶菜类',1,NULL,1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(9,'根茎类',1,NULL,2,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(10,'茄果类',1,NULL,3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(11,'瓜类',1,NULL,4,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(12,'菌菇类',1,NULL,5,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(13,'苹果',2,NULL,1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(14,'柑橘橙',2,NULL,2,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(15,'热带水果',2,NULL,3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(16,'浆果类',2,NULL,4,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(17,'瓜果类',2,NULL,5,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(18,'猪肉',3,NULL,1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(19,'牛肉',3,NULL,2,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(20,'羊肉',3,NULL,3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(21,'禽肉',3,NULL,4,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(22,'蛋类',3,NULL,5,1,'2026-02-17 12:14:35','2026-02-17 12:14:35');
/*!40000 ALTER TABLE `prod_category` ENABLE KEYS */;

--
-- Table structure for table `prod_product`
--

DROP TABLE IF EXISTS `prod_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prod_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `subtitle` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `main_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主图',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '商品图片JSON',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `detail` text COLLATE utf8mb4_unicode_ci COMMENT '商品详情HTML',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态:0-下架,1-上架,2-审核中',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核,1-已通过,2-已拒绝',
  `audit_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prod_product`
--

/*!40000 ALTER TABLE `prod_product` DISABLE KEYS */;
INSERT INTO `prod_product` VALUES (1,1,8,'有机菠菜','新鲜有机菠菜 500g 当日采摘','https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400','[\"https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400\"]',8.00,10.00,197,59,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(2,1,9,'农家土豆','黄心土豆 1000g 粉糯香甜','https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400','[\"https://images.unsplash.com/photo-1518977676601-b53f82ber64f?w=400\"]',6.50,8.00,149,90,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,1,10,'新鲜番茄','大红番茄 500g 酸甜可口','https://images.unsplash.com/photo-1546470427-227c7369a9b8?w=400','[\"https://images.unsplash.com/photo-1546470427-227c7369a9b8?w=400\"]',7.00,9.00,180,123,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(4,1,11,'黄瓜','新鲜刺黄瓜 500g 脆嫩爽口','https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400','[\"https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400\"]',5.50,7.00,200,67,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,1,12,'香菇','新鲜香菇 250g 肉厚味美','https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400','[\"https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400\"]',12.00,15.00,80,45,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(6,1,13,'红富士苹果','陕西红富士苹果 5斤装 脆甜多汁','https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400','[\"https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400\"]',29.90,38.00,96,238,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(7,1,14,'赣南脐橙','江西赣南脐橙 5斤装 皮薄肉厚','https://images.unsplash.com/photo-1547514701-42782101795e?w=400','[\"https://images.unsplash.com/photo-1547514701-42782101795e?w=400\"]',35.00,45.00,80,167,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(8,1,18,'土鸡蛋','散养土鸡蛋 30枚 营养健康','https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?w=400','[\"https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?w=400\"]',35.00,42.00,150,312,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(9,2,13,'红富士苹果','烟台红富士 5斤装 特级果','https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400','[\"https://images.unsplash.com/photo-1579613832125-5d34a13ffe2a?w=400\"]',32.00,42.00,200,456,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(10,2,14,'砂糖橘','广西砂糖橘 3斤装 甜蜜多汁','https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400','[\"https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400\"]',25.00,30.00,150,289,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(11,2,15,'芒果','海南大台农芒果 3斤装 香甜可口','https://images.unsplash.com/photo-1553279768-865429fa0078?w=400','[\"https://images.unsplash.com/photo-1553279768-865429fa0078?w=400\"]',28.00,35.00,100,178,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(12,2,16,'蓝莓','智利进口蓝莓 125g 新鲜直达','https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400','[\"https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400\"]',18.00,22.00,60,145,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(13,2,17,'哈密瓜','新疆西州蜜哈密瓜 1个 网纹香甜','https://images.unsplash.com/photo-1595366392896-5bb28e3a0062?w=400','[\"https://images.unsplash.com/photo-1595366392896-5bb28e3a0062?w=400\"]',15.00,20.00,80,98,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(14,2,8,'生菜','新鲜球生菜 300g 沙拉专用','https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400','[\"https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400\"]',4.50,6.00,120,87,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(15,3,4,'基围虾','活冻基围虾 500g 鲜活速冻','https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400','[\"https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400\"]',45.00,55.00,50,0,NULL,1,0,NULL,'2026-02-17 12:14:35','2026-03-14 01:53:00'),(16,3,4,'大黄鱼','新鲜大黄鱼 500g 肉质鲜美','https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=400','[\"https://images.unsplash.com/photo-1534043464124-3be32fe000c9?w=400\"]',38.00,48.00,40,0,NULL,1,0,NULL,'2026-02-17 12:14:35','2026-03-14 01:53:00'),(17,3,4,'扇贝','鲜活扇贝 1kg 肥美多汁','https://images.unsplash.com/photo-1618221835733-1b989ca30038?w=400','[\"https://images.unsplash.com/photo-1618221835733-1b989ca30038?w=400\"]',68.00,85.00,30,0,NULL,1,0,NULL,'2026-02-17 12:14:35','2026-03-14 01:53:00'),(18,4,18,'咸鸭蛋','高邮咸鸭蛋 10枚 流油起沙','https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400','[\"https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400\"]',25.00,30.00,100,0,NULL,0,2,'产品图片与描述不符，请重新上传','2026-02-17 12:14:35','2026-02-17 12:14:35'),(19,4,20,'土鸭蛋','散养土鸭蛋 15枚 营养丰富','https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400','[\"https://images.unsplash.com/photo-1583224944047-0c9a7e0f366d?w=400\"]',30.00,38.00,80,0,NULL,0,2,'缺少生产日期标识','2026-02-17 12:14:35','2026-02-17 12:14:35'),(20,5,5,'东北大米','五常大米 5kg 香软可口','https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400','[\"https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400\"]',65.00,78.00,200,445,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(21,5,5,'花生油','古法花生油 5L 香味浓郁','https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400','[\"https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400\"]',128.00,158.00,80,234,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(22,5,5,'酱油','特级生抽酱油 500ml 鲜味十足','https://tse2.mm.bing.net/th/id/OIP.yWjuTtVHuNeqF4Jr1VACdgHaD0?rs=1&pid=ImgDetMain&o=7&rm=3','[\"https://tse2.mm.bing.net/th/id/OIP.yWjuTtVHuNeqF4Jr1VACdgHaD0?rs=1&pid=ImgDetMain&o=7&rm=3\"]',12.00,15.00,300,567,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-03-13 15:16:57'),(23,5,5,'山西老陈醋','山西老陈醋 500ml 酸香醇厚','https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400','[\"https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=400\"]',15.00,18.00,150,321,NULL,1,1,NULL,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(24,1,6,'纯牛奶','纯牛奶 250ml*24盒 营养健康','https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400','[]',58.00,68.00,198,681,'',1,1,NULL,'2026-02-17 12:14:35','2026-03-14 00:00:54');
/*!40000 ALTER TABLE `prod_product` ENABLE KEYS */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `user_type` tinyint NOT NULL DEFAULT '1' COMMENT '用户类型:1-普通用户,2-商家,3-管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=290741374188441601 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','超级管理员','13800000001',NULL,'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(2,'admin02','运营管理员','13800000002',NULL,'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',3,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,'user01','张三','13800138001','https://api.dicebear.com/7.x/avataaars/svg?seed=1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(4,'user02','李四','13800138002','https://api.dicebear.com/7.x/avataaars/svg?seed=2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',1,1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,'merchant01','绿色农场','13900139001','https://api.dicebear.com/7.x/initials/svg?seed=GF','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',2,1,'2026-02-17 12:14:35','2026-03-13 23:33:41'),(6,'merchant02','田园果业','13900139002','https://api.dicebear.com/7.x/initials/svg?seed=TY','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',2,1,'2026-02-17 12:14:35','2026-03-13 23:41:58'),(7,'merchant03','深海渔港','13900139003','https://api.dicebear.com/7.x/initials/svg?seed=SH','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',2,1,'2026-02-17 12:14:35','2026-03-13 23:41:58'),(8,'merchant04','山里禽蛋','13900139004','https://api.dicebear.com/7.x/initials/svg?seed=SL','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',2,1,'2026-02-17 12:14:35','2026-03-13 23:41:58'),(9,'merchant05','粮油世家','13900139005','https://api.dicebear.com/7.x/initials/svg?seed=LY','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',2,1,'2026-02-17 12:14:35','2026-03-13 23:41:58');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货电话',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `district` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区',
  `detail_address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,3,'张三','13800138001','北京市','北京市','朝阳区','朝阳区建国路88号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(2,3,'张三','13800138001','北京市','北京市','海淀区','海淀区中关村大街1号',0,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(3,4,'李四','13800138002','上海市','上海市','浦东新区','浦东新区世纪大道100号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(4,4,'李四','13800138002','上海市','上海市','黄浦区','黄浦区南京东路200号',0,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(5,5,'王五','13800138003','广东省','广州市','天河区','天河区天河路123号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(6,6,'赵六','13800138004','广东省','深圳市','南山区','南山区科技园南区',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(7,7,'孙七','13800138005','浙江省','杭州市','西湖区','西湖区文一西路456号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(8,8,'周八','13800138006','江苏省','南京市','鼓楼区','鼓楼区中山路789号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(9,9,'吴九','13800138007','四川省','成都市','武侯区','武侯区天府大道1000号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35'),(10,10,'郑十','13800138008','湖北省','武汉市','江汉区','江汉区解放大道666号',1,'2026-02-17 12:14:35','2026-02-17 12:14:35');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;

--
-- Dumping routines for database 'agriproduct'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-16 12:33:21
