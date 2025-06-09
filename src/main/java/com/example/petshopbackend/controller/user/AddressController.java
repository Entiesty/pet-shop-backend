package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.entity.Address;
import com.example.petshopbackend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户收货地址模块", description = "提供对登录用户收货地址的增删改查功能")
@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "获取当前用户的所有收货地址", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<Address> addresses = addressService.getAddressesByUsername(userDetails.getUsername());
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "为当前用户新增收货地址", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        Address savedAddress = addressService.addAddress(address, userDetails.getUsername());
        return ResponseEntity.ok(savedAddress);
    }

    @Operation(summary = "更新指定ID的收货地址", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
            @Parameter(description = "要更新的地址ID") @PathVariable Long id,
            @RequestBody Address address,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Address updatedAddress = addressService.updateAddress(id, address, userDetails.getUsername());
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "删除指定ID的收货地址", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "要删除的地址ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        addressService.deleteAddress(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "设置指定ID的地址为默认地址", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/default") // 使用PATCH更符合RESTful风格，因为只修改部分属性
    public ResponseEntity<Void> setDefaultAddress(
            @Parameter(description = "要设为默认的地址ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        addressService.setDefaultAddress(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}