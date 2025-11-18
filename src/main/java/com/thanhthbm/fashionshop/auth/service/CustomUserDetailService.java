package com.thanhthbm.fashionshop.auth.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.repository.UserDetailRepository;
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
}
