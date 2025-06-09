package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.CartDtos;
import com.example.petshopbackend.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户购物车模块", description = "提供购物车商品管理功能")
@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "获取我的购物车详情", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<CartDtos.CartViewDto> getMyCart(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shoppingCartService.getCartForUser(userDetails.getUsername()));
    }

    @Operation(summary = "添加或更新购物车中的商品数量", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/items")
    public ResponseEntity<Void> addOrUpdateItem(@RequestBody CartDtos.CartItemAddDto itemDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        shoppingCartService.addOrUpdateItem(itemDto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "从购物车中移除指定商品", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@Parameter(description = "要移除的商品ID") @PathVariable Long productId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        shoppingCartService.removeItem(productId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}