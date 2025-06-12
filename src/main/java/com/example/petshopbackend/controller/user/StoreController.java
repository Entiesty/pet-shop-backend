package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.NearbyStoreDto;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商店浏览模块", description = "提供面向所有用户的商店查询功能")
@RestController
@RequestMapping("/api/user/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "查询附近的宠物商店", description = "这是一个公开接口，无需登录即可访问")
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyStoreDto>> getNearbyStores(
            @Parameter(description = "查询中心的经度", required = true, example = "116.404") @RequestParam("lng") Double lng,
            @Parameter(description = "查询中心的纬度", required = true, example = "39.915") @RequestParam("lat") Double lat,
            @Parameter(description = "搜索半径（公里）", example = "10") @RequestParam(value = "distance", defaultValue = "5") Double distance
    ) {
        List<NearbyStoreDto> nearbyStores = storeService.findNearbyStores(lng, lat, distance);
        return ResponseEntity.ok(nearbyStores);
    }

    /**
     * [ADDED] 获取单个商店的完整详情（包括部分商品）
     */
    @Operation(summary = "获取单个商店详情", description = "公开接口，返回指定ID商店的完整信息及其部分商品")
    @GetMapping("/{id}")
    public ResponseEntity<UserDtos.UserStoreDetailViewDto> getStoreDetail(
            @Parameter(description = "商店的唯一ID") @PathVariable Long id
    ) {
        UserDtos.UserStoreDetailViewDto storeDetail = storeService.getStoreDetailForUser(id);
        return ResponseEntity.ok(storeDetail);
    }
}