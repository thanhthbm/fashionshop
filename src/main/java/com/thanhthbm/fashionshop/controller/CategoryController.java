package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.dto.CategoryDTO;
import com.thanhthbm.fashionshop.entity.Category;
import com.thanhthbm.fashionshop.service.CategoryService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }


  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Category>> getCategoryById(@RequestParam(value = "id") UUID id) {
    Category category = this.categoryService.getCategory(id);
    return ResponseEntity.ok().body(ApiResponse.success(category));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody CategoryDTO categoryDTO) {
    Category category = this.categoryService.createCategory(categoryDTO);
    return new ResponseEntity<>(ApiResponse.created(category), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
    List<Category> categories = categoryService.getAllCategories();
    return new ResponseEntity<>(ApiResponse.success(categories), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable(value = "id") UUID categoryId, @RequestBody CategoryDTO categoryDTO) {
    Category category = categoryService.updateCategory(categoryDTO, categoryId);
    return new ResponseEntity<>(ApiResponse.success(category), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable(value = "id") UUID categoryId) {
    categoryService.deleteCategory(categoryId);
    return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
  }

}
