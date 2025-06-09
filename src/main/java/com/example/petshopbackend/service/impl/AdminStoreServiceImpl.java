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
    // 💡 重要提示：这是一个涉及两个不同数据库的操作。
    // 严格来说，保证跨数据库的事务一致性需要引入JTA（如Atomikos）等分布式事务方案。
    // 在本实训项目中，我们简化处理，假设这两个操作会连续成功。
    @Transactional
    public void createStore(AdminDtos.StoreDto storeDto) {
        // 1. 创建并保存MySQL中的Store记录
        Store store = new Store();
        store.setName(storeDto.getName());
        store.setAddressText(storeDto.getAddressText());
        store.setLogoUrl(storeDto.getLogoUrl());
        store.setContactPhone(storeDto.getContactPhone());
        baseMapper.insert(store); // 插入后，store对象的ID会被自动填充

        // 2. 创建并保存MongoDB中的StoreLocation记录
        StoreLocation storeLocation = new StoreLocation();
        storeLocation.setStoreId(store.getId());
        storeLocation.setName(store.getName());
        GeoJsonPoint point = new GeoJsonPoint(storeDto.getLongitude(), storeDto.getLatitude());
        storeLocation.setLocation(point);
        storeLocationRepository.save(storeLocation);
    }

    @Override
    @Transactional
    public void updateStore(Long id, AdminDtos.StoreDto storeDto) {
        // 1. 更新MySQL中的记录
        Store store = baseMapper.selectById(id);
        if (store == null) {
            throw new RuntimeException("商店不存在");
        }
        store.setName(storeDto.getName());
        store.setAddressText(storeDto.getAddressText());
        store.setLogoUrl(storeDto.getLogoUrl());
        store.setContactPhone(storeDto.getContactPhone());
        baseMapper.updateById(store);

        // 2. 更新MongoDB中的记录
        StoreLocation storeLocation = storeLocationRepository.findByStoreId(id);
        if (storeLocation != null) {
            storeLocation.setName(storeDto.getName());
            GeoJsonPoint point = new GeoJsonPoint(storeDto.getLongitude(), storeDto.getLatitude());
            storeLocation.setLocation(point);
            storeLocationRepository.save(storeLocation);
        }
    }
}
