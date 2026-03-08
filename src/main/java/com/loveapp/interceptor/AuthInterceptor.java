package com.loveapp.interceptor;

import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import com.loveapp.utils.JwtUtils;
import com.loveapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            throw new BusinessException(ResultCode.NOT_LOGIN);
        }
        
        String token = authHeader.substring(TOKEN_PREFIX.length());
        
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        // 将用户ID存入ThreadLocal
        UserContext.setUserId(userId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 清理ThreadLocal
        UserContext.clear();
    }
}
