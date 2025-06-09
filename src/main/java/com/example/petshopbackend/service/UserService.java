package com.example.petshopbackend.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.User;

public interface UserService extends IService<User> {

    UserDtos.UserProfileDto getUserProfileByUsername(String username);
}
