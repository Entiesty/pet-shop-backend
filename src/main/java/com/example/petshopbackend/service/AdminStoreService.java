package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Store;

public interface AdminStoreService extends IService<Store> {

    void createStore(AdminDtos.StoreDto storeDto);

    void updateStore(Long id, AdminDtos.StoreDto storeDto);

    // 分页查询和删除功能，可以直接使用继承自IService的方法，无需在此重复定义
}
