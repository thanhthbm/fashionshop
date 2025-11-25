package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.entity.CategoryType;
import com.thanhthbm.fashionshop.repository.CategoryTypeRepository;
import com.turkraft.springfilter.boot.Filter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category-type")
public class CategoryTypeController {

  @Autowired
  private CategoryTypeRepository categoryTypeRepository;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryType>>> getAll(
      @Filter Specification<CategoryType> spec
  ) {
    return ResponseEntity.ok(ApiResponse.success(categoryTypeRepository.findAll(spec)));
  }
}
