package com.thanhthbm.fashionshop.auth.repository;

import com.thanhthbm.fashionshop.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);
}
