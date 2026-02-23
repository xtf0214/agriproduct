package com.agriproduct.enums;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // ========== 用户相关 1000-1999 ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    USER_PHONE_EXISTS(1005, "手机号已被注册"),

    // ========== 认证授权 2000-2999 ==========
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_MISSING(2003, "Token缺失"),
    PERMISSION_DENIED(2004, "权限不足"),

    // ========== 商品相关 3000-3999 ==========
    PRODUCT_NOT_FOUND(3001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(3002, "商品库存不足"),
    PRODUCT_ALREADY_DELETED(3003, "商品已删除"),
    PRODUCT_AUDIT_PENDING(3004, "商品审核中"),

    // ========== 订单相关 4000-4999 ==========
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(4003, "订单无法取消"),
    ORDER_CANNOT_PAY(4004, "订单无法支付"),

    // ========== 购物车相关 5000-5999 ==========
    CART_ITEM_NOT_FOUND(5001, "购物车商品不存在"),
    CART_ITEM_LIMIT_EXCEEDED(5002, "超出购买限制"),

    // ========== 商家相关 6000-6999 ==========
    MERCHANT_NOT_FOUND(6001, "商家不存在"),
    MERCHANT_AUDIT_PENDING(6002, "商家审核中"),
    MERCHANT_AUDIT_REJECTED(6003, "商家审核未通过"),

    // ========== 文件上传 7000-7999 ==========
    FILE_UPLOAD_ERROR(7001, "文件上传失败"),
    FILE_TYPE_ERROR(7002, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(7003, "文件大小超限"),
    FILE_NOT_FOUND(7004, "文件不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
