package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.CartDtos;
import com.example.petshopbackend.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<CartDtos.CartViewDto> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shoppingCartService.getCartForUser(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addOrUpdateItem(@RequestBody CartDtos.CartItemAddDto itemDto, @AuthenticationPrincipal UserDetails userDetails) {
        shoppingCartService.addOrUpdateItem(itemDto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        shoppingCartService.removeItem(productId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
