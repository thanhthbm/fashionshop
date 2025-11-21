package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.entity.WishList;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, UUID> {
  List<WishList> findByUserId(UUID userId);

  boolean existsByUserIdAndProductId(UUID userId, UUID productId);

  @Transactional
  void deleteByUserIdAndProductId(UUID userId, UUID productId);
}
