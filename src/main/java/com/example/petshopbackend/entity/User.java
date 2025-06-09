package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // [ADDED]

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@TableName("users")
public class User implements UserDetails { // [MODIFIED] 实现 UserDetails 接口

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String openid;
    private Integer role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 以下是实现 UserDetails 接口所需要的方法

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据role字段返回对应的权限角色
        // 注意：角色需要以 "ROLE_" 开头
        String roleName = role == 1 ? "ROLE_ADMIN" : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    // getPassword() 和 getUsername() 方法 lombok 的 @Data 注解已经为我们生成了

    @Override
    public boolean isAccountNonExpired() {
        // 账户是否未过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 账户是否未被锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 凭证（密码）是否未过期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 账户是否可用
        return true;
    }
}