package org.example.ieltstyper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils; // 确保你之前已经创建了 JwtUtils 类

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头中获取 Authorization 字段
        String authHeader = request.getHeader("Authorization");

        // 2. 检查 Token 是否以 "Bearer " 开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 截取掉 "Bearer " 之后的部分

            // 3. 验证 Token 是否有效
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);

                // 4. 如果 Token 有效，在 SecurityContext 中设置身份信息
                // 这里我们简单处理，不走数据库，直接信任 Token 里的用户名
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 5. 继续执行后面的过滤器链
        filterChain.doFilter(request, response);
    }
}