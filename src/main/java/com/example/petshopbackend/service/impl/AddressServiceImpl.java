package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.entity.Address;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.AddressMapper;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Address> getAddressesByUserId(Long userId) {
        // 直接根据用户ID查询地址列表
        return baseMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
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

    /**
     * [ADDED] 更新收货地址
     */
    @Override
    public Address updateAddress(Long addressId, Address newAddressData, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Address existingAddress = baseMapper.selectById(addressId);

        // 核心安全校验：确保地址存在且属于当前登录用户
        if (existingAddress == null || !existingAddress.getUserId().equals(user.getId())) {
            throw new RuntimeException("地址不存在或无权修改");
        }

        // 更新字段
        existingAddress.setContactName(newAddressData.getContactName());
        existingAddress.setPhone(newAddressData.getPhone());
        existingAddress.setProvince(newAddressData.getProvince());
        existingAddress.setCity(newAddressData.getCity());
        existingAddress.setDistrict(newAddressData.getDistrict());
        existingAddress.setStreet(newAddressData.getStreet());
        existingAddress.setIsDefault(newAddressData.getIsDefault());

        baseMapper.updateById(existingAddress);
        return existingAddress;
    }

    /**
     * [ADDED] 删除收货地址
     */
    @Override
    public void deleteAddress(Long addressId, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Address address = baseMapper.selectById(addressId);

        // 核心安全校验
        if (address == null || !address.getUserId().equals(user.getId())) {
            throw new RuntimeException("地址不存在或无权删除");
        }

        baseMapper.deleteById(addressId);
    }

    /**
     * [ADDED] 设置默认收货地址
     */
    @Override
    @Transactional // 保证事务一致性，两个更新操作要么都成功，要么都失败
    public void setDefaultAddress(Long addressId, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Address address = baseMapper.selectById(addressId);

        // 核心安全校验
        if (address == null || !address.getUserId().equals(user.getId())) {
            throw new RuntimeException("地址不存在或无权操作");
        }

        // 1. 将该用户的所有地址都设置为非默认
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, user.getId()).set(Address::getIsDefault, false);
        baseMapper.update(null, updateWrapper);

        // 2. 将指定的地址设置为默认
        address.setIsDefault(true);
        baseMapper.updateById(address);
    }
}
