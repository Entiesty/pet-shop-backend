package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Store;

public interface AdminStoreService extends IService<Store> {
    void createStore(AdminDtos.StoreDto storeDto);
    void updateStore(Long id, AdminDtos.StoreDto storeDto);
    void deleteStoreAndLocation(Long id);
}
