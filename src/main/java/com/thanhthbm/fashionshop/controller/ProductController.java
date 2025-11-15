package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.dto.ProductRequest;
import com.thanhthbm.fashionshop.dto.ResultPaginationDTO;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.service.ProductService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class ProductController {
  private ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<?>> getAllProducts(
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) UUID categoryTypeId,
      @RequestParam(required = false) String slug,
      @RequestParam(required = false) Boolean isNewArrival,
      Pageable pageable) {

    List<ProductDTO> productList = new ArrayList<>();

    if (StringUtils.isNotBlank(slug)) {
      ProductDTO productDTO = productService.getProductBySlug(slug, pageable);
      productList.add(productDTO);
      return ResponseEntity.ok().body(ApiResponse.success(productList));
    }

    ProductRequest productRequest = ProductRequest.builder()
        .categoryId(categoryId)
        .categoryTypeId(categoryTypeId)
        .isNewArrival(isNewArrival)
        .pageable(pageable)
        .build();

    com.thanhthbm.fashionshop.dto.ResultPaginationDTO resultPaginationDTO = productService.getAllProducts(productRequest);
    return ResponseEntity.ok().body(ApiResponse.success(resultPaginationDTO));

  }

  @PostMapping
  public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductDTO productDTO) {
    Product product = productService.addProduct(productDTO);
    return new ResponseEntity<>(ApiResponse.created(product), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
    ProductDTO productDTO = productService.getProductById(id);
    return ResponseEntity.ok().body(ApiResponse.success(productDTO));
  }

  @PutMapping
  public ResponseEntity<ApiResponse<Product>> updateProduct(@RequestBody ProductDTO productDTO) {
    Product product = productService.updateProduct(productDTO);

    return new ResponseEntity<>(ApiResponse.success(product), HttpStatus.OK);
  }

}
