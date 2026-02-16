package com.agriproduct.common.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token工具类
 */
public class JwtUtils {

    /**
     * 默认密钥（生产环境应从配置文件读取）
     */
    private static final String DEFAULT_SECRET = "agriproduct-jwt-secret-key-2024-spring-boot-vue-miniprogram";

    /**
     * 默认过期时间（7天）
     */
    private static final long DEFAULT_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 密钥
     */
    private static SecretKey secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 用户ID声明
     */
    private static final String CLAIM_USER_ID = "userId";

    /**
     * 用户类型声明
     */
    private static final String CLAIM_USER_TYPE = "userType";

    /**
     * 生成Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userType 用户类型
     * @return Token字符串
     */
    public static String generateToken(Long userId, String username, Integer userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USER_TYPE, userType);
        return createToken(claims, username);
    }

    /**
     * 创建Token
     */
    private static String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + DEFAULT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从Token中提取用户ID
     */
    public static Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_USER_ID, Long.class));
    }

    /**
     * 从Token中提取用户类型
     */
    public static Integer extractUserType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_USER_TYPE, Integer.class));
    }

    /**
     * 从Token中提取用户名
     */
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从Token中提取过期时间
     */
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 提取指定声明
     */
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取所有声明
     */
    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查Token是否过期
     */
    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 验证Token
     */
    public static Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 验证Token（不验证用户名）
     */
    public static Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新Token
     */
    public static String refreshToken(String token) {
        final Claims claims = extractAllClaims(token);
        return createToken(new HashMap<>(claims), claims.getSubject());
    }

    /**
     * 设置密钥
     */
    public static void setSecretKey(String secret) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
