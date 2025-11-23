package com.thanhthbm.fashionshop.auth.repository;

import com.thanhthbm.fashionshop.auth.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends JpaRepository<User, UUID> {
  User findByEmail(String email);
}
