package com.example.petshopbackend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Order;
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

    @Operation(summary = "分页查询订单列表", description = "可根据订单号或状态进行筛选")
    @GetMapping
    public ResponseEntity<Page<Order>> getOrderList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "订单号 (可选, 模糊查询)") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态 (可选)") @RequestParam(required = false) Integer status
    ) {
        Page<Order> page = adminOrderService.listOrders(new Page<>(current, size), orderNo, status);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{orderNo}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "订单号") @PathVariable String orderNo,
            @RequestBody AdminDtos.AdminOrderStatusUpdateDto statusDto
    ) {
        adminOrderService.updateOrderStatus(orderNo, statusDto);
        return ResponseEntity.ok().build();
    }
}