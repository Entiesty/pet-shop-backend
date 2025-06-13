// [MODIFIED]
package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; // [ADDED]
import com.example.petshopbackend.dto.NearbyStoreDto;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.entity.StoreLocation;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.repository.mongo.StoreLocationRepository;
import com.example.petshopbackend.service.ProductService;
import com.example.petshopbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ProductService productService; // 注入ProductService以便查询商品
    // [ADDED] 定义一个日志记录器
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreMapper storeMapper; // 确保StoreMapper已注入

    @Override
    public List<NearbyStoreDto> findNearbyStores(Double longitude, Double latitude, Double distanceInKm) {
        // [LOG] 打印收到的参数，确认参数传递正确
        log.info("开始查询附近商店，输入参数 -> 经度(lng): {}, 纬度(lat): {}, 距离(distance): {}km",
                longitude, latitude, distanceInKm);

        // --- 第一步：使用 MongoDB 进行高效的地理位置筛选 ---
        Point centerPoint = new Point(longitude, latitude);
        Distance distance = new Distance(distanceInKm, Metrics.KILOMETERS);

        log.info("正在向MongoDB发送地理位置查询...");
        List<GeoResult<StoreLocation>> geoResults = storeLocationRepository.findByLocationNear(centerPoint, distance).getContent();

        // [LOG] 打印从MongoDB查询到的结果数量，这是最关键的诊断点之一！
        log.info("MongoDB查询完成，找到 {} 个地理位置匹配的商店。", geoResults.size());

        if (CollectionUtils.isEmpty(geoResults)) {
            log.info("由于MongoDB未返回任何结果，流程提前结束。");
            return Collections.emptyList();
        }

        // --- 第二步：使用 MyBatis-Plus 批量查询商店的详细信息 ---
        List<Long> storeIds = geoResults.stream()
                .map(result -> result.getContent().getStoreId())
                .collect(Collectors.toList());

        // [LOG] 打印从MongoDB结果中提取出的所有storeId
        log.info("从MongoDB结果中提取出的Store ID列表: {}", storeIds);

        log.info("正在向MySQL批量查询 {} 个商店的详细信息...", storeIds.size());
        Map<Long, Store> storeDetailsMap = baseMapper.selectBatchIds(storeIds).stream()
                .collect(Collectors.toMap(Store::getId, Function.identity()));

        // [LOG] 打印从MySQL查询到的结果数量，这是另一个关键诊断点！
        log.info("MySQL批量查询完成，根据ID找到 {} 个商店的详细信息。", storeDetailsMap.size());

        // --- 第三步：组合数据，生成最终的 DTO 列表 ---
        List<NearbyStoreDto> finalResult = geoResults.stream()
                .map(geoResult -> {
                    Store storeDetails = storeDetailsMap.get(geoResult.getContent().getStoreId());
                    // 如果MySQL中找不到对应ID的商店，storeDetails会是null
                    if (storeDetails == null) {
                        log.warn("数据不一致：在MongoDB中找到了storeId {}，但在MySQL中没有找到对应的商店信息！",
                                geoResult.getContent().getStoreId());
                    }
                    return NearbyStoreDto.from(geoResult, storeDetails);
                })
                .collect(Collectors.toList());

        // [LOG] 打印最终返回的记录数
        log.info("数据组合完成，最终返回 {} 条商店记录。", finalResult.size());

        return finalResult;
    }

    @Override
    public UserDtos.UserStoreDetailViewDto getStoreDetailForUser(Long storeId) {
        // 1. 从MySQL查询基础信息
        Store store = this.getById(storeId);
        if (store == null) {
            throw new RuntimeException("商店不存在");
        }

        // 2. 从MongoDB查询地理位置信息
        StoreLocation storeLocation = storeLocationRepository.findByStoreId(storeId);

        // 3. [MODIFIED] 查询该商店下的第一页商品，并接收新的返回类型
        Page<ProductDtos.ProductListViewDto> productsPage = productService.listProducts(new Page<>(1, 10), storeId, null, null);

        // 4. 组装成DTO
        UserDtos.UserStoreDetailViewDto dto = new UserDtos.UserStoreDetailViewDto();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setAddressText(store.getAddressText());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setContactPhone(store.getContactPhone());

        if (storeLocation != null && storeLocation.getLocation() != null) {
            dto.setLongitude(storeLocation.getLocation().getX());
            dto.setLatitude(storeLocation.getLocation().getY());
        }

        // 5. [MODIFIED] 将新的DTO分页对象设置进去
        dto.setProducts(productsPage);

        return dto;
    }
}