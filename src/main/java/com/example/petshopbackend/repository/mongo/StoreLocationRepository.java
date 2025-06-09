package com.example.petshopbackend.repository.mongo;

import com.example.petshopbackend.entity.StoreLocation;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StoreLocationRepository extends MongoRepository<StoreLocation, String> {
    // 根据一个点和距离，查询附近的位置
    GeoResults<StoreLocation> findByLocationNear(Point point, Distance distance);

    StoreLocation findByStoreId(Long storeId); // [ADDED]
}
