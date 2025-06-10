package com.example.petshopbackend.config;

import com.example.petshopbackend.exception.AccessDeniedHandlerImpl;
import com.example.petshopbackend.exception.AuthExceptionEntryPoint;
import com.example.petshopbackend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthExceptionEntryPoint authExceptionEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS配置
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authExceptionEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // [MODIFIED] 将公开浏览的接口也加入 permitAll() 列表中
                        .requestMatchers(
                                "/api/user/auth/**",      // 认证接口(注册/登录)
                                "/doc.html",              // API文档
                                "/swagger-ui.html",       // API文档
                                "/swagger-ui/**",         // API文档
                                "/v3/api-docs/**"         // API文档
                        ).permitAll()
                        // [ADDED] 更精确地放行公开的GET请求
                        .requestMatchers(HttpMethod.GET,
                                "/api/user/stores/**",    // 允许任何人GET商店信息
                                "/api/user/products/**",  // 允许任何人GET商品信息
                                "/api/user/reviews/**"    // 允许任何人GET评价信息
                        ).permitAll()
                        // [ADDED] 添加OPTIONS请求放行，解决预检请求问题
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 管理员接口
                        .anyRequest().authenticated() // 其他所有请求都需要认证
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 跨域配置
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5175")); // 前端地址
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 明确指定允许的方法
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With")); // 明确指定允许的请求头
        configuration.setExposedHeaders(List.of("Authorization", "role", "username", "status")); // 可暴露的自定义响应头
        configuration.setAllowCredentials(true); // 允许携带凭证
        configuration.setMaxAge(3600L); // 预检请求缓存时间为1小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 应用于所有路径

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
