package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("address")
@Schema(description = "收货地址实体")
public class Address {
    @TableId(type = IdType.AUTO)
    @Schema(description = "地址唯一ID (更新时需要)")
    private Long id;

    @Schema(description = "所属用户的ID (由后端自动填充)")
    private Long userId;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Schema(description = "省份", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Schema(description = "区/县", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    @Schema(description = "详细街道地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @Schema(description = "是否为默认地址 (true/false)")
    private Boolean isDefault;

    @Schema(description = "创建时间 (由后端自动填充)")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间 (由后端自动填充)")
    private LocalDateTime updatedAt;
}