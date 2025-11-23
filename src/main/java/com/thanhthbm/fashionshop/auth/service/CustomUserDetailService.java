package com.thanhthbm.fashionshop.auth.service;

import com.thanhthbm.fashionshop.auth.dto.UserDetailsDTO;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.repository.UserDetailRepository;
import com.thanhthbm.fashionshop.dto.User.UserProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

  @Autowired
  private UserDetailRepository userDetailRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDetailRepository.findByEmail(username);

    if (null == user) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }

    return user;
  }

  public User handleUpdateUser(User user) {
    return userDetailRepository.save(user);
  }

  public UserDetailsDTO updateUserProfile(User user, UserProfileRequest userProfileRequest) {
    user.setFirstName(userProfileRequest.getFirstName());
    user.setLastName(userProfileRequest.getLastName());

    if (null != userProfileRequest.getPhoneNumber()) {
      user.setPhoneNumber(userProfileRequest.getPhoneNumber());
    }

    this.userDetailRepository.save(user);

    return UserDetailsDTO.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .id(user.getId())
        .phoneNumber(user.getPhoneNumber())
        .build();
  }
}
