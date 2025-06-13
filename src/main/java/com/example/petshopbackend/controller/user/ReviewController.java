package com.example.petshopbackend.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户评价模块", description = "提供商品评价的创建和查询功能")
@RestController
@RequestMapping("/api/user/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "提交商品评价", description = "用户对任意商品进行评价", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody ReviewDtos.ReviewCreateDto createDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.createReview(createDto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "查询商品的评价列表", description = "这是一个公开接口，无需登录即可访问")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDtos.ReviewViewDto>> getProductReviews(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size
    ) {
        Page<Object> page = new Page<>(current, size);
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId, page));
    }
}
