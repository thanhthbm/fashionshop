package com.thanhthbm.fashionshop.auth.controller;

import com.thanhthbm.fashionshop.auth.dto.UserDetailsDTO;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.dto.ApiResponse;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserDetailController {
  @Autowired
  private CustomUserDetailService customUserDetailService;

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserDetailsDTO>> getUserProfile(Principal principal) {
    User user = (User) customUserDetailService.loadUserByUsername(principal.getName());

    if (null == user) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .id(user.getId())
        .phoneNumber(user.getPhoneNumber())
        .authorityList(user.getAuthorities().toArray())
        .addressList(user.getAddressList())
        .build();
    return new ResponseEntity(ApiResponse.success(userDetailsDTO), HttpStatus.OK);
  }
}
