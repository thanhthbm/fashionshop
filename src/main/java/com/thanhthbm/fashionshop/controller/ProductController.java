package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.service.ProductService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID categoryTypeId) {
    List<Product> productList = productService.getAllProducts(categoryId, categoryTypeId);
    return ResponseEntity.ok().body(productList);
  }

  @PostMapping
  public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
    Product product = productService.addProduct(productDTO);
    return new ResponseEntity<>(product, HttpStatus.CREATED);
  }
}
