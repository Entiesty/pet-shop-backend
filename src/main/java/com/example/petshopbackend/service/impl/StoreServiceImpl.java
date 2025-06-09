// [MODIFIED]
package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; // [ADDED]
import com.example.petshopbackend.dto.NearbyStoreDto;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.entity.StoreLocation;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.repository.mongo.StoreLocationRepository;
import com.example.petshopbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商店业务逻辑的实现类 (已重构)
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService { // [MODIFIED] 继承 ServiceImpl

    // 只需注入非MyBatis-Plus部分的依赖
    private final StoreLocationRepository storeLocationRepository;

    @Override
    public List<NearbyStoreDto> findNearbyStores(Double longitude, Double latitude, Double distanceInKm) {
        // --- 第一步：MongoDB 地理位置筛选 (逻辑不变) ---
        Point centerPoint = new Point(longitude, latitude);
        Distance distance = new Distance(distanceInKm, Metrics.KILOMETERS);
        List<GeoResult<StoreLocation>> geoResults = storeLocationRepository.findByLocationNear(centerPoint, distance).getContent();

        if (CollectionUtils.isEmpty(geoResults)) {
            return Collections.emptyList();
        }

        // --- 第二步：MyBatis-Plus 批量查询 (逻辑优化) ---
        List<Long> storeIds = geoResults.stream()
                .map(result -> result.getContent().getStoreId())
                .collect(Collectors.toList());

        // [MODIFIED] 使用 `baseMapper` (由ServiceImpl提供) 代替 `storeMapper`
        Map<Long, Store> storeDetailsMap = baseMapper.selectBatchIds(storeIds).stream()
                .collect(Collectors.toMap(Store::getId, Function.identity()));

        // --- 第三步：组合数据 (逻辑优化) ---
        // [MODIFIED] 使用DTO的静态工厂方法进行转换，代码更简洁
        return geoResults.stream()
                .map(geoResult -> {
                    Store storeDetails = storeDetailsMap.get(geoResult.getContent().getStoreId());
                    return NearbyStoreDto.from(geoResult, storeDetails);
                })
                .collect(Collectors.toList());
    }
}