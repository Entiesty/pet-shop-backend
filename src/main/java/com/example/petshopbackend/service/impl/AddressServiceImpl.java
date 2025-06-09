package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.entity.Address;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.AddressMapper;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    private final UserMapper userMapper;

    @Override
    public List<Address> getAddressesByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return baseMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUserId, user.getId()));
    }

    @Override
    public Address addAddress(Address address, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        address.setUserId(user.getId());
        baseMapper.insert(address);
        return address;
    }
}
