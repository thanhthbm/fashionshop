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
        .receiverName(addressRequest.getReceiverName())
        .phoneNumber(addressRequest.getPhoneNumber())
        .detail(addressRequest.getDetail())
        .ward(addressRequest.getWard())
        .district(addressRequest.getDistrict())
        .province(addressRequest.getProvince())
        .user(user)
        .build();

    return addressRepository.save(address);
  }

}
