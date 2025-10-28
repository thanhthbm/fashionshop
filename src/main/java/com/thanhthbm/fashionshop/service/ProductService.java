package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import java.util.List;
import java.util.UUID;

public interface ProductService {
  public Product addProduct(ProductDTO product);
  public List<ProductDTO> getAllProducts(UUID categoryId, UUID categoryTypeId);

  ProductDTO getProductBySlug(String slug);

  ProductDTO getProductById(UUID id);

  Product updateProduct(ProductDTO productDTO);
}
