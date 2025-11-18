package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.dto.ProductRequest;
import com.thanhthbm.fashionshop.dto.ResultPaginationDTO;
import com.thanhthbm.fashionshop.entity.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
  public Product addProduct(ProductDTO product);
  ResultPaginationDTO getAllProducts(Specification<Product> spec, Pageable pageable);

  ProductDTO getProductBySlug(String slug, Pageable pageable);

  ProductDTO getProductById(UUID id);

  Product updateProduct(ProductDTO productDTO);

  Product fetchProductById(UUID id);

  List<ProductDTO> getAllNewProducts(Boolean isNewArrival);

}
