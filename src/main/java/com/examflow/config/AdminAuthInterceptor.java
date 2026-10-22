package com.examflow.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String ADMIN_SESSION_KEY = "adminUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow login page and login form submission
        if (uri.equals("/admin/login") || uri.startsWith("/admin/login")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(ADMIN_SESSION_KEY) != null) {
            return true;
        }

        // Redirect unauthenticated admin access to login page
        response.sendRedirect("/admin/login?error=required");
        return false;
    }
}
