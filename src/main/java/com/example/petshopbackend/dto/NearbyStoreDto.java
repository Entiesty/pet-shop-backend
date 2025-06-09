// [MODIFIED]
package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.entity.StoreLocation;
import lombok.Data;
import org.springframework.data.geo.GeoResult;

@Data
public class NearbyStoreDto {

    private Long storeId;
    private String name;
    private String addressText;
    private String logoUrl;
    private double distance;
    private double longitude;
    private double latitude;

    /**
     * [ADDED] 静态工厂方法，用于将实体转换为DTO
     * @param geoResult 包含MongoDB地理位置和距离的结果
     * @param storeDetails 包含MySQL商店详情的实体
     * @return 组装好的NearbyStoreDto对象
     */
    public static NearbyStoreDto from(GeoResult<StoreLocation> geoResult, Store storeDetails) {
        StoreLocation storeLocation = geoResult.getContent();
        NearbyStoreDto dto = new NearbyStoreDto();

        // 设置来自MongoDB的数据
        dto.setStoreId(storeLocation.getStoreId());
        dto.setDistance(geoResult.getDistance().getValue());
        dto.setLongitude(storeLocation.getLocation().getX());
        dto.setLatitude(storeLocation.getLocation().getY());

        // 设置来自MySQL的数据
        if (storeDetails != null) {
            dto.setName(storeDetails.getName());
            dto.setAddressText(storeDetails.getAddressText());
            dto.setLogoUrl(storeDetails.getLogoUrl());
        } else {
            // 降级策略
            dto.setName(storeLocation.getName());
        }
        return dto;
    }
}