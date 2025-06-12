package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.entity.StoreLocation;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.repository.mongo.StoreLocationRepository;
import com.example.petshopbackend.service.AdminStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements AdminStoreService {

    private final StoreLocationRepository storeLocationRepository;

    @Override
    @Transactional
    public void createStore(AdminDtos.StoreDto storeDto) {
        Store store = new Store();
        store.setName(storeDto.getName());
        store.setAddressText(storeDto.getAddressText());
        store.setLogoUrl(storeDto.getLogoUrl());
        store.setContactPhone(storeDto.getContactPhone());
        baseMapper.insert(store);

        StoreLocation storeLocation = new StoreLocation();
        storeLocation.setStoreId(store.getId());
        storeLocation.setName(store.getName());
        storeLocation.setLocation(new GeoJsonPoint(storeDto.getLongitude(), storeDto.getLatitude()));
        storeLocationRepository.save(storeLocation);
    }

    @Override
    @Transactional
    public void updateStore(Long id, AdminDtos.StoreDto storeDto) {
        Store store = baseMapper.selectById(id);
        if (store == null) throw new RuntimeException("商店不存在");

        store.setName(storeDto.getName());
        store.setAddressText(storeDto.getAddressText());
        store.setLogoUrl(storeDto.getLogoUrl());
        store.setContactPhone(storeDto.getContactPhone());
        baseMapper.updateById(store);

        StoreLocation storeLocation = storeLocationRepository.findByStoreId(id);
        if (storeLocation != null) {
            storeLocation.setName(storeDto.getName());
            storeLocation.setLocation(new GeoJsonPoint(storeDto.getLongitude(), storeDto.getLatitude()));
            storeLocationRepository.save(storeLocation);
        }
    }

    @Override
    @Transactional
    public void deleteStoreAndLocation(Long id) {
        // 先删除MySQL中的记录
        baseMapper.deleteById(id);
        // 再删除MongoDB中的记录
        storeLocationRepository.deleteByStoreId(id);
    }

    /**
     * [ADDED] 实现获取商店聚合详情的逻辑
     */
    @Override
    public AdminDtos.AdminStoreDetailViewDto getStoreDetailById(Long storeId) {
        // 1. 从MySQL查询基础信息
        Store store = baseMapper.selectById(storeId);
        if (store == null) {
            throw new RuntimeException("商店不存在");
        }

        // 2. 从MongoDB查询地理位置信息
        StoreLocation storeLocation = storeLocationRepository.findByStoreId(storeId);

        // 3. 组装成DTO
        AdminDtos.AdminStoreDetailViewDto dto = new AdminDtos.AdminStoreDetailViewDto();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setAddressText(store.getAddressText());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setContactPhone(store.getContactPhone());

        // 如果MongoDB中有对应的位置数据，则填充经纬度
        if (storeLocation != null && storeLocation.getLocation() != null) {
            dto.setLongitude(storeLocation.getLocation().getX());
            dto.setLatitude(storeLocation.getLocation().getY()); // 添加设置纬度的代码
        }

        return dto;
    }
}
