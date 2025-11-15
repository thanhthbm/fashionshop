package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.AddressRequest;
import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.service.AddressService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
public class AddressController {

  @Autowired
  private AddressService addressService;

  @PostMapping
  public ResponseEntity<ApiResponse<Address>> createAddress(@RequestBody AddressRequest addressRequest, Principal principal) {
    return  ResponseEntity.ok(ApiResponse.created(addressService.createAddress(addressRequest, principal)));
  }

}
