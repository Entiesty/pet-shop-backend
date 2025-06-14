package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService; // 导入 IService
import com.example.petshopbackend.dto.NearbyStoreDto;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.Store; // 导入实体类

import java.util.List;

/**
 * 商店相关业务逻辑的服务接口
 * 继承 IService<Store> 以获得基础的CRUD能力
 */
public interface StoreService extends IService<Store> { // 继承 IService

    /**
     * 这是我们的自定义业务方法，不受继承影响
     */
    List<NearbyStoreDto> findNearbyStores(Double longitude, Double latitude, Double distanceInKm);
    /**
     * [ADDED] 获取单个商店的聚合详情（供用户端使用）
     * @param storeId 商店ID
     * @return 聚合后的商店详情DTO
     */
    UserDtos.UserStoreDetailViewDto getStoreDetailForUser(Long storeId);
}
