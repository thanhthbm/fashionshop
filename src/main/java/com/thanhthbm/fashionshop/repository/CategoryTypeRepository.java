package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.entity.CategoryType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryTypeRepository extends JpaRepository<CategoryType, UUID> {
  List<CategoryType> findAll(Specification<CategoryType> spec);
}
