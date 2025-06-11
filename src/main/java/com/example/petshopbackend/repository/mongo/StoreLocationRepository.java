package com.example.petshopbackend.repository.mongo;

import com.example.petshopbackend.entity.StoreLocation;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StoreLocationRepository extends MongoRepository<StoreLocation, String> {
    GeoResults<StoreLocation> findByLocationNear(Point point, Distance distance);
    StoreLocation findByStoreId(Long storeId);
    void deleteByStoreId(Long storeId);
}
