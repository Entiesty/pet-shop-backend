package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.WalletDtos;
import com.example.petshopbackend.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "用户钱包模块", description = "提供余额充值与查询功能")
@RestController
@RequestMapping("/api/user/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "查询我的余额", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getMyBalance(@AuthenticationPrincipal UserDetails userDetails) {
        BigDecimal balance = walletService.getBalance(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @Operation(summary = "为我的钱包充值", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/recharge")
    public ResponseEntity<String> rechargeMyWallet(@RequestBody WalletDtos.RechargeDto rechargeDto, @AuthenticationPrincipal UserDetails userDetails) {
        walletService.recharge(userDetails.getUsername(), rechargeDto.getAmount());
        return ResponseEntity.ok("充值成功");
    }
}
