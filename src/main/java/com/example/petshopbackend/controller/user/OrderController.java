package com.example.petshopbackend.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.OrderDtos;
import com.example.petshopbackend.entity.Order;
import com.example.petshopbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户订单模块", description = "提供订单创建和查询功能")
@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "从购物车创建订单", description = "根据选择的购物车项和地址创建新订单", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderDtos.OrderCreateDto createDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        Order newOrder = orderService.createOrder(createDto, userDetails.getUsername());
        return ResponseEntity.ok(newOrder.getOrderNo());
    }

    @Operation(summary = "分页获取当前用户的历史订单", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<Page<Order>> getMyOrders(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Page<Order> page = new Page<>(current, size);
        return ResponseEntity.ok(orderService.listUserOrders(userDetails.getUsername(), page));
    }

    @Operation(summary = "根据订单号获取订单详情", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderDtos.OrderViewDto> getOrderDetails(
            @Parameter(description = "订单的唯一编号") @PathVariable String orderNo,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderNo, userDetails.getUsername()));
    }

    @Operation(summary = "使用余额支付订单", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{orderNo}/pay")
    public ResponseEntity<String> payOrder(
            @Parameter(description = "要支付的订单号") @PathVariable String orderNo,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        orderService.payForOrder(orderNo, userDetails.getUsername());
        return ResponseEntity.ok("支付成功，等待发货");
    }

    @Operation(summary = "用户确认收货", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{orderNo}/confirm-receipt")
    public ResponseEntity<String> confirmReceipt(
            @Parameter(description = "要确认收货的订单号") @PathVariable String orderNo,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        orderService.confirmReceipt(orderNo, userDetails.getUsername());
        return ResponseEntity.ok("确认收货成功，订单已完成");
    }
}