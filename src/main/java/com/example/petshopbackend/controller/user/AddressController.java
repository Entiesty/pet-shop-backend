package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.entity.Address;
import com.example.petshopbackend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        List<Address> addresses = addressService.getAddressesByUsername(userDetails.getUsername());
        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address, @AuthenticationPrincipal UserDetails userDetails) {
        Address savedAddress = addressService.addAddress(address, userDetails.getUsername());
        return ResponseEntity.ok(savedAddress);
    }
}
