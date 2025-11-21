package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.AddressRequest;
import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.service.AddressService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping
  public ResponseEntity<ApiResponse<List<Address>>> getAddresses(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(ApiResponse.success(addressService.getAddresses(user)));
  }

  @PutMapping("/{addressId}")
  public ResponseEntity<ApiResponse<Address>> updateAddress(@PathVariable UUID addressId, @AuthenticationPrincipal User user, @RequestBody AddressRequest addressRequest) {
    return ResponseEntity.ok(ApiResponse.success(addressService.updateAddress(addressId, addressRequest, user)));
  }

  @DeleteMapping("/{addressId}")
  public ResponseEntity<ApiResponse<Void>> deleteAddress(@AuthenticationPrincipal User user, @PathVariable UUID addressId) {
    addressService.deleteAddress(addressId, user);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{addressId}/default")
  public ResponseEntity<ApiResponse<Void>> setDefaultAddress(@AuthenticationPrincipal User user, @PathVariable UUID addressId) {
    addressService.setDefaultAddress(addressId, user);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

}
