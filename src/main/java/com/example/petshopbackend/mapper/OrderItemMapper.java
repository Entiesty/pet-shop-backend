package com.example.petshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopbackend.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {}
