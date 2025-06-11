package com.example.petshopbackend.controller.admin;

import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-订单管理模块", description = "提供订单发货等管理功能")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "订单发货")
    @PostMapping("/{orderNo}/ship")
    public ResponseEntity<String> shipOrder(
            @Parameter(description = "要发货的订单号") @PathVariable String orderNo,
            @RequestBody AdminDtos.OrderShipmentDto shipmentDto) {
        adminOrderService.shipOrder(orderNo, shipmentDto);
        return ResponseEntity.ok("发货成功");
    }
}