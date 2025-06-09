package com.example.petshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}
