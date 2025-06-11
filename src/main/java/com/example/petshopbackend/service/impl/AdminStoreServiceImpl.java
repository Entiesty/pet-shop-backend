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
}
