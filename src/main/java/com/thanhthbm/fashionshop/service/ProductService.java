package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import java.util.List;
import java.util.UUID;

public interface ProductService {
  public Product addProduct(ProductDTO product);
  public List<Product> getAllProducts(UUID categoryId, UUID categoryTypeId);

}
