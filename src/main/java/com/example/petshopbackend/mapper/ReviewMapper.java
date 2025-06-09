package com.example.petshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopbackend.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价表的MyBatis-Plus Mapper接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}
