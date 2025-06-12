package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Store;

public interface AdminStoreService extends IService<Store> {
    void createStore(AdminDtos.StoreDto storeDto);
    void updateStore(Long id, AdminDtos.StoreDto storeDto);
    void deleteStoreAndLocation(Long id);
    /**
     * [ADDED] 获取单个商店的聚合详情（包含经纬度）
     * @param storeId 商店ID
     * @return 聚合后的商店详情DTO
     */
    AdminDtos.AdminStoreDetailViewDto getStoreDetailById(Long storeId);
}
