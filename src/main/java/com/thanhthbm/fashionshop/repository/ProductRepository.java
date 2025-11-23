package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.entity.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

  Product findBySlug(String slug);

  List<Product> findByIsNewArrival(boolean b);
}
