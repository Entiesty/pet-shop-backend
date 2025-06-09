package com.example.petshopbackend.controller.user; // [MODIFIED] 包路径已更新

import com.example.petshopbackend.dto.NearbyStoreDto;
import com.example.petshopbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商店相关功能的API控制器 (面向用户)
 */
@RestController
@RequestMapping("/api/user/stores") // [MODIFIED] API基础路径已更新
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * API端点: GET /api/user/stores/nearby
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyStoreDto>> getNearbyStores(
            @RequestParam("lng") Double lng,
            @RequestParam("lat") Double lat,
            @RequestParam(value = "distance", defaultValue = "5") Double distance
    ) {
        List<NearbyStoreDto> nearbyStores = storeService.findNearbyStores(lng, lat, distance);
        return ResponseEntity.ok(nearbyStores);
    }
}
