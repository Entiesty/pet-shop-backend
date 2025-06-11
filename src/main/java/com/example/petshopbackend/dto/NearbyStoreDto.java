package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.entity.StoreLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.geo.GeoResult;

@Data
@Schema(description = "附近商店信息的DTO")
public class NearbyStoreDto {

    @Schema(description = "商店ID", example = "101")
    private Long storeId;

    @Schema(description = "商店名称", example = "爱宠之家")
    private String name;

    @Schema(description = "商店详细地址", example = "XX市XX区人民路123号")
    private String addressText;

    @Schema(description = "商店Logo图片URL", example = "http://example.com/logo/101.png")
    private String logoUrl;

    @Schema(description = "距离（单位由GeoApi设定，通常是公里）", example = "1.25")
    private double distance;

    @Schema(description = "经度", example = "121.473701")
    private double longitude;

    @Schema(description = "纬度", example = "31.230416")
    private double latitude;

    /**
     * 静态工厂方法，用于将实体转换为DTO
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