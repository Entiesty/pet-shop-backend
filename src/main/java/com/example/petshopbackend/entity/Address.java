package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("address")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String contactName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String street;
    private Boolean isDefault; // tinyint(1) 映射为 Boolean 更直观
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
