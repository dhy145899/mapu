package com.hniu.mapu.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 * 用于拦截需要管理员权限的请求
 */
@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        
        // 获取用户角色
        Integer userRole = (Integer) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");
        
        // 检查是否已登录
        if (userId == null) {
            log.warn("未登录用户尝试访问管理页面: {}", request.getRequestURI());
            response.sendRedirect("/admin/login");
            return false;
        }
        
        // 检查是否为管理员
        if (userRole == null || userRole != 1) {
            log.warn("普通用户尝试访问管理页面: userId={}, role={}, uri={}", userId, userRole, request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"权限不足，仅管理员可访问\",\"data\":null}");
            return false;
        }
        
        return true;
    }
}