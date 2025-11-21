package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.dto.AddressRequest;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        .province(addressRequest.getProvince())
        .user(user)
        .isDefault(false)
        .build();

    return addressRepository.save(address);
  }

  public List<Address> getAddresses(User user) {
    return this.addressRepository.findAddressByUser(user);
  }

  public Address updateAddress(UUID addressId, AddressRequest addressRequest, User user) {
    Optional<Address> addressOptional = addressRepository.findByUserAndId(user, addressId);
    if (!addressOptional.isPresent()) {
      throw new ResourceNotFoundException("Address not found");
    }

    Address address = addressOptional.get();
    address.setReceiverName(addressRequest.getReceiverName());
    address.setPhoneNumber(addressRequest.getPhoneNumber());
    address.setDetail(addressRequest.getDetail());
    address.setWard(addressRequest.getWard());
    address.setProvince(addressRequest.getProvince());
    address.setUser(user);
    return addressRepository.save(address);
  }

  public void deleteAddress(UUID addressId, User user) {
    Optional<Address> addressOptional = addressRepository.findByUserAndId(user, addressId);
    if (!addressOptional.isPresent()) {
      throw new ResourceNotFoundException("Address not found");
    }

    addressRepository.deleteById(addressId);
  }

  public void setDefaultAddress(UUID addressId, User user) {
    Optional<Address> addressOptional = addressRepository.findByUserAndId(user, addressId);
    if (!addressOptional.isPresent()) {
      throw new ResourceNotFoundException("Address not found");
    }

    List<Address> addresses = this.getAddresses(user);

    for (Address address : addresses) {
      if (address.getId().equals(addressId)) {
        address.setIsDefault(true);
      }
      else  {
        address.setIsDefault(false);
      }
    }

    this.addressRepository.saveAll(addresses);
  }
}
