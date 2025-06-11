package com.example.petshopbackend.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.entity.Address;
import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> getAddressesByUsername(String username);
    Address addAddress(Address address, String username);
    // [ADDED] 新增方法签名
    Address updateAddress(Long addressId, Address address, String username);
    void deleteAddress(Long addressId, String username);
    void setDefaultAddress(Long addressId, String username);
    // 添加根据用户ID获取地址列表的方法
    List<Address> getAddressesByUserId(Long userId);
}
