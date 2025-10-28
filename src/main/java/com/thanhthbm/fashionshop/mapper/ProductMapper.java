package com.thanhthbm.fashionshop.mapper;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.dto.ProductResourceDTO;
import com.thanhthbm.fashionshop.dto.ProductVariantDTO;
import com.thanhthbm.fashionshop.entity.Category;
import com.thanhthbm.fashionshop.entity.CategoryType;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.entity.Resources;
import com.thanhthbm.fashionshop.service.CategoryService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  @Autowired
  private CategoryService categoryService;


  public Product mapToProductEntity(ProductDTO productDTO) {
    Product product = new Product();
    if (productDTO.getId() != null) {
      product.setId(productDTO.getId());
    }

    product.setName(productDTO.getName());
    product.setPrice(productDTO.getPrice());
    product.setDescription(productDTO.getDescription());
    product.setBrand(productDTO.getBrand());
    product.setNewArrival(productDTO.isNewArrival());
    product.setRating(productDTO.getRating());
    product.setSlug(productDTO.getSlug());

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


    return product;
  }

  public List<Resources> mapToProductResources(List<ProductResourceDTO> productResources, Product product) {

    return productResources.stream().map(productResourceDTO -> {
      Resources resources = new Resources();
      if (null != productResourceDTO.getId()) {
        resources.setId(productResourceDTO.getId());
      }

      resources.setName(productResourceDTO.getName());
      resources.setType(productResourceDTO.getType());
      resources.setUrl(productResourceDTO.getUrl());
      resources.setIsPrimary(productResourceDTO.getIsPrimary());
      resources.setProduct(product);
      return resources;
    }).collect(Collectors.toList());
  }

  public List<ProductVariant> mapToProductVariant(List<ProductVariantDTO> productVariantDTOs, Product product) {
    return productVariantDTOs.stream().map(productVariantDTO -> {
      ProductVariant productVariant = new ProductVariant();

      if (null != productVariantDTO.getId()) {
        productVariant.setId(productVariantDTO.getId());
      }

      productVariant.setColor(productVariantDTO.getColor());
      productVariant.setSize(productVariantDTO.getSize());
      productVariant.setStockQuantity(productVariantDTO.getStockQuantity());
      productVariant.setProduct(product);
      return productVariant;
    }).collect(Collectors.toList());
  }

  public List<ProductDTO> getProductDTOs(List<Product> products) {
    return products.stream().map(this::mapProductToDTO).toList();
  }

  public ProductDTO mapProductToDTO(Product product) {

    return ProductDTO.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .description(product.getDescription())
        .brand(product.getBrand())
        .isNewArrival(product.isNewArrival())
        .rating(product.getRating())
        .thumbnail(getProductThumbnail(product.getResources()))
        .slug(product.getSlug())
        .build();
  }

  private String getProductThumbnail(List<Resources> resources) {
    return resources.stream().filter(Resources :: getIsPrimary).findFirst().orElse(null).getUrl();
  }

  public List<ProductVariantDTO> mapProductVariantListToDTO(List<ProductVariant> productVariants) {
    return productVariants.stream().map(this::mapProductVariantDTO).toList();
  }

  private ProductVariantDTO mapProductVariantDTO(ProductVariant productVariant) {
    return ProductVariantDTO.builder()
        .color(productVariant.getColor())
        .id(productVariant.getId())
        .size(productVariant.getSize())
        .stockQuantity(productVariant.getStockQuantity())
        .build();
  }

  public List<ProductResourceDTO> mapProductResourcesListToDTO(List<Resources> resources) {
    return resources.stream().map(this::mapResourceToDTO).toList();
  }

  private ProductResourceDTO mapResourceToDTO(Resources resources) {
    return ProductResourceDTO.builder()
        .id(resources.getId())
        .url(resources.getUrl())
        .name(resources.getName())
        .isPrimary(resources.getIsPrimary())
        .type(resources.getType())
        .build();
  }
}
