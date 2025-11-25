package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.dto.Format.ResultPaginationDTO;
import com.thanhthbm.fashionshop.dto.Product.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.service.Product.ProductService;
import com.turkraft.springfilter.boot.Filter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ResponseEntity<ApiResponse<?>> getAllProducts(
      @Filter Specification<Product> specification,
      Pageable pageable) {

    ResultPaginationDTO resultPaginationDTO = productService.getAllProducts(specification, pageable);
    return ResponseEntity.ok().body(ApiResponse.success(resultPaginationDTO));

  }

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductDTO productDTO) {
    Product product = productService.addProduct(productDTO);
    return new ResponseEntity<>(ApiResponse.created(product), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
    ProductDTO productDTO = productService.getProductById(id);
    return ResponseEntity.ok().body(ApiResponse.success(productDTO));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable("id") UUID id, @RequestBody ProductDTO productDTO) {
    Product product = productService.updateProduct(id, productDTO);

    return new ResponseEntity<>(ApiResponse.success(product), HttpStatus.OK);
  }

}
