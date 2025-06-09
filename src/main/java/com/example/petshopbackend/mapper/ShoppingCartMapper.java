package com.example.petshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopbackend.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车表的MyBatis-Plus Mapper接口
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
