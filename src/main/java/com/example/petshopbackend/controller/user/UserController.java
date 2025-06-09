package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserProfileDto> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserDtos.UserProfileDto userProfile = userService.getUserProfileByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }
}
