package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT v FROM ProductVariant v WHERE v.id = :id")
  Optional<ProductVariant> findByIdWithLock(@Param("id") UUID id);

  @Query("SELECT v.stockQuantity FROM ProductVariant v WHERE v.id = :id")
  Integer getStockQuantityById(@Param("id") UUID id);
}
