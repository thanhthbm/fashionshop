package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.dto.ProductResourceDTO;
import com.thanhthbm.fashionshop.dto.ProductVariantDTO;
import com.thanhthbm.fashionshop.entity.Category;
import com.thanhthbm.fashionshop.entity.CategoryType;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.entity.Resources;
import com.thanhthbm.fashionshop.repository.ProductRepository;
import com.thanhthbm.fashionshop.specification.ProductSpecification;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryService categoryService;

  @Override
  public Product addProduct(ProductDTO productDTO) {
    Product product = mapToProductEntity(productDTO);
    return productRepository.save(product);
  }

  @Override
  public List<Product> getAllProducts(UUID categoryId, UUID categoryTypeId) {
    Specification<Product> productSpecification = Specification.allOf();

    if (null != categoryId) {
      productSpecification = productSpecification.and(ProductSpecification.hasCategoryId(categoryId));
    }
    if (null != categoryTypeId) {
      productSpecification = productSpecification.and(ProductSpecification.hasCategoryTypeId(categoryTypeId));
    }

    List<Product> products = productRepository.findAll(productSpecification);
    //to do mapping of product into productDTO
    return products;
  }

  private Product mapToProductEntity(ProductDTO productDTO) {
    Product product = new Product();
    product.setName(productDTO.getName());
    product.setPrice(productDTO.getPrice());
    product.setDescription(productDTO.getDescription());
    product.setBrand(productDTO.getBrand());
    product.setNewArrival(productDTO.isNewArrival());
    product.setRating(productDTO.getRating());

    Category category = categoryService.getCategory(productDTO.getCategoryId());

    if (null != category) {
      product.setCategory(category);
      UUID categoryTypeId =  productDTO.getCategoryTypeId();

      CategoryType categoryType = category.getCategoryTypes().stream().filter(categoryType1 -> categoryType1.getId().equals(categoryTypeId)).findFirst().orElse(null);
      product.setCategoryType(categoryType);
    }
    if (null != productDTO.getVariants()) {
      product.setProductVariants(mapToProductVariant(productDTO.getVariants(), product));
    }

    if (null != productDTO.getProductResources()){
      product.setResources(mapToProductResources(productDTO.getProductResources(), product));
    }


    return productRepository.save(product);
  }

  private List<Resources> mapToProductResources(List<ProductResourceDTO> productResources, Product product) {

    return productResources.stream().map(productResourceDTO -> {
      Resources resources = new Resources();
      resources.setName(productResourceDTO.getName());
      resources.setType(productResourceDTO.getType());
      resources.setUrl(productResourceDTO.getUrl());
      resources.setIsPrimary(productResourceDTO.getIsPrimary());
      resources.setProduct(product);
      return resources;
    }).collect(Collectors.toList());
  }

  private List<ProductVariant> mapToProductVariant(List<ProductVariantDTO> productVariantDTOs, Product product) {
    return productVariantDTOs.stream().map(productVariantDTO -> {
      ProductVariant productVariant = new ProductVariant();
      productVariant.setColor(productVariantDTO.getColor());
      productVariant.setSize(productVariantDTO.getSize());
      productVariant.setStockQuantity(productVariantDTO.getStockQuantity());
      productVariant.setProduct(product);
      return productVariant;
    }).collect(Collectors.toList());
  }
}
