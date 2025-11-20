package com.thanhthbm.fashionshop.service;


import com.thanhthbm.fashionshop.repository.ProductVariantRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
  private final ProductVariantRepository productVariantRepository;

  public Integer getStockQuantityByID(UUID id) {
    return productVariantRepository.getStockQuantityById(id);
  }
}
