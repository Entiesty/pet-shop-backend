package com.example.petshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopbackend.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {}