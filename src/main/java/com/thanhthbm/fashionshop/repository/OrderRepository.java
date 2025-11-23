package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.entity.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByUserOrderByOrderDateDesc(User user);
}
