package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.AddressRequest;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final UserDetailsService userDetailsService;
  private final AddressRepository addressRepository;

  @Transactional
  public Address createAddress(AddressRequest addressRequest, Principal principal) {
    User user = (User) userDetailsService.loadUserByUsername(principal.getName());

    if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
      addressRepository.resetAllDefaultAddresses(user.getId());
    }

    Address address = Address.builder()
        .receiverName(addressRequest.getReceiverName())
        .phoneNumber(addressRequest.getPhoneNumber())
        .detail(addressRequest.getDetail())
        .ward(addressRequest.getWard())
        .province(addressRequest.getProvince())
        .user(user)
        .isDefault(addressRequest.getIsDefault())
        .build();


     if (addressRepository.countByUser(user) == 0) { address.setIsDefault(true); }

    return addressRepository.save(address);
  }

  public List<Address> getAddresses(User user) {
    return this.addressRepository.findAddressByUser(user);
  }

  @Transactional
  public Address updateAddress(UUID addressId, AddressRequest addressRequest, User user) {
    Address address = addressRepository.findByUserAndId(user, addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

    if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
      addressRepository.resetAllDefaultAddresses(user.getId());
    }

    address.setReceiverName(addressRequest.getReceiverName());
    address.setPhoneNumber(addressRequest.getPhoneNumber());
    address.setDetail(addressRequest.getDetail());
    address.setWard(addressRequest.getWard());
    address.setProvince(addressRequest.getProvince());
    address.setIsDefault(addressRequest.getIsDefault());

    return addressRepository.save(address);
  }

  @Transactional
  public void deleteAddress(UUID addressId, User user) {
    Address address = addressRepository.findByUserAndId(user, addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

    addressRepository.delete(address);
  }

  @Transactional
  public void setDefaultAddress(UUID addressId, User user) {
    Address address = addressRepository.findByUserAndId(user, addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

    addressRepository.resetAllDefaultAddresses(user.getId());

    address.setIsDefault(true);
    addressRepository.save(address);
  }
}