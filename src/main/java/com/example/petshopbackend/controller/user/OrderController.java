package com.example.petshopbackend.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.OrderDtos;
import com.example.petshopbackend.entity.Order;
import com.example.petshopbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderDtos.OrderCreateDto createDto, @AuthenticationPrincipal UserDetails userDetails) {
        Order newOrder = orderService.createOrder(createDto, userDetails.getUsername());
        // 返回新订单的订单号
        return ResponseEntity.ok(newOrder.getOrderNo());
    }

    /**
     * [ADDED] 获取当前用户的订单列表（分页）
     * @param current 当前页码，默认为1
     * @param size 每页数量，默认为10
     */
    @GetMapping
    public ResponseEntity<Page<Order>> getMyOrders(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Page<Order> page = new Page<>(current, size);
        return ResponseEntity.ok(orderService.listUserOrders(userDetails.getUsername(), page));
    }

    /**
     * [ADDED] 获取单个订单的详细信息
     * @param orderNo 订单号
     */
    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderDtos.OrderViewDto> getOrderDetails(
            @PathVariable String orderNo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderNo, userDetails.getUsername()));
    }
}
