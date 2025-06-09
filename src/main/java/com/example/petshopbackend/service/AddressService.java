package com.example.petshopbackend.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.entity.Address;
import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> getAddressesByUsername(String username);
    Address addAddress(Address address, String username);
}
