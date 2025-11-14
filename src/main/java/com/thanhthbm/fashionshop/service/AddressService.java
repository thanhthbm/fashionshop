package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.AddressRequest;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AddressRepository addressRepository;

  public Address createAddress(AddressRequest addressRequest, Principal principal) {
    com.thanhthbm.fashionshop.auth.entity.User user = (com.thanhthbm.fashionshop.auth.entity.User) userDetailsService.loadUserByUsername(principal.getName());
    Address address = Address.builder()
        .state(addressRequest.getState())
        .zipCode(addressRequest.getZipCode())
        .phoneNumber(addressRequest.getPhoneNumber())
        .city(addressRequest.getCity())
        .street(addressRequest.getStreet())
        .user(user)
        .build();

    return addressRepository.save(address);
  }

}
