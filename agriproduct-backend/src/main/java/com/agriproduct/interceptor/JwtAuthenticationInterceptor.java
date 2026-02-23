package com.agriproduct.interceptor;

import com.agriproduct.exception.BusinessException;
import com.agriproduct.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_TYPE_HEADER = "X-User-Type";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = extractToken(request);
        if (token == null) {
            throw new BusinessException("未登录或Token已过期");
        }

        // 验证Token
        if (!JwtUtils.validateToken(token)) {
            throw new BusinessException("Token验证失败");
        }

        // 提取用户信息并设置到请求头
        Long userId = JwtUtils.extractUserId(token);
        String username = JwtUtils.extractUsername(token);
        Integer userType = JwtUtils.extractUserType(token);

        request.setAttribute(USER_ID_HEADER, userId);
        request.setAttribute(USERNAME_HEADER, username);
        request.setAttribute(USER_TYPE_HEADER, userType);

        log.debug("JWT认证成功: userId={}, username={}, userType={}", userId, username, userType);
        return true;
    }

    /**
     * 从请求头中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            return authHeader.substring(AUTH_PREFIX.length());
        }
        return null;
    }
}
