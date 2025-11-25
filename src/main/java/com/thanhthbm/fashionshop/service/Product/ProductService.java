package com.thanhthbm.fashionshop.service.Product;

import com.thanhthbm.fashionshop.dto.Product.ProductDTO;
import com.thanhthbm.fashionshop.dto.Format.ResultPaginationDTO;
import com.thanhthbm.fashionshop.entity.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {
  public Product addProduct(ProductDTO product);
  ResultPaginationDTO getAllProducts(Specification<Product> spec, Pageable pageable);

  ProductDTO getProductBySlug(String slug, Pageable pageable);

  ProductDTO getProductById(UUID id);

  @Transactional
  Product updateProduct(UUID id, ProductDTO productDTO);

  Product fetchProductById(UUID id);

  List<ProductDTO> getAllNewProducts(Boolean isNewArrival);

}
