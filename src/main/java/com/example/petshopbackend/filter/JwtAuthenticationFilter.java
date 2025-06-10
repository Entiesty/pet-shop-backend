package com.example.petshopbackend.filter;

import com.example.petshopbackend.service.RedisService;
import com.example.petshopbackend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        log.debug("处理请求: {} {}", request.getMethod(), requestURI);
        
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                log.debug("未找到JWT令牌，继续过滤链: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            String username = null;
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                log.warn("无效的JWT令牌: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("JWT令牌有效，正在验证用户: {}", username);
                
                // 从Redis获取令牌进行比较
                String tokenFromRedis = redisService.get("login_token:" + username);
                
                // 令牌有效性验证
                if (token.equals(tokenFromRedis) && !jwtUtil.isTokenExpired(token)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("用户已认证: {}, 角色: {}", username, userDetails.getAuthorities());
                } else {
                    if (tokenFromRedis == null) {
                        log.warn("Redis中未找到用户令牌，可能已登出: {}", username);
                    } else if (jwtUtil.isTokenExpired(token)) {
                        log.warn("令牌已过期: {}", username);
                    } else {
                        log.warn("令牌不匹配: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理JWT认证时发生异常", e);
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}