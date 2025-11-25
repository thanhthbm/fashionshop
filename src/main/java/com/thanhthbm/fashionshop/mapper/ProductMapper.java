package com.thanhthbm.fashionshop.mapper;

import com.thanhthbm.fashionshop.dto.Product.ProductDTO;
import com.thanhthbm.fashionshop.dto.Product.ProductResourceDTO;
import com.thanhthbm.fashionshop.dto.Product.ProductVariantDTO;
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
    product.setIsNewArrival(productDTO.isNewArrival());
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

  public void updateProductFromDTO(ProductDTO dto, Product entity) {
    entity.setName(dto.getName());
    entity.setPrice(dto.getPrice());
    entity.setDescription(dto.getDescription());
    entity.setBrand(dto.getBrand());
    entity.setIsNewArrival(dto.isNewArrival());
    entity.setSlug(dto.getSlug());

    if (entity.getCategory() == null || !entity.getCategory().getId().equals(dto.getCategoryId())) {
      Category category = categoryService.getCategory(dto.getCategoryId());
      entity.setCategory(category);
    }
    if (dto.getCategoryTypeId() != null) {
      CategoryType type = entity.getCategory().getCategoryTypes().stream()
          .filter(t -> t.getId().equals(dto.getCategoryTypeId()))
          .findFirst().orElse(null);
      entity.setCategoryType(type);
    }

    if (dto.getVariants() != null) {
      List<ProductVariant> currentVariants = entity.getProductVariants();
      List<ProductVariantDTO> incomingVariants = dto.getVariants();

      currentVariants.removeIf(current ->
          incomingVariants.stream().noneMatch(in -> in.getId() != null && in.getId().equals(current.getId()))
      );

      for (ProductVariantDTO in : incomingVariants) {
        if (in.getId() == null) {
          ProductVariant newV = ProductVariant.builder()
              .color(in.getColor())
              .size(in.getSize())
              .stockQuantity(in.getStockQuantity())
              .product(entity)
              .build();
          currentVariants.add(newV);
        } else {
          currentVariants.stream()
              .filter(curr -> curr.getId().equals(in.getId()))
              .findFirst()
              .ifPresent(curr -> {
                curr.setColor(in.getColor());
                curr.setSize(in.getSize());
                curr.setStockQuantity(in.getStockQuantity());
              });
        }
      }
    }

    if (dto.getProductResources() != null) {
      List<Resources> currentRes = entity.getResources();
      List<ProductResourceDTO> incomingRes = dto.getProductResources();

      currentRes.removeIf(current ->
          incomingRes.stream().noneMatch(in -> in.getId() != null && in.getId().equals(current.getId()))
      );

      for (ProductResourceDTO in : incomingRes) {
        if (in.getId() == null) {
          Resources newRes = Resources.builder()
              .name(in.getName())
              .url(in.getUrl())
              .type(in.getType())
              .isPrimary(in.getIsPrimary())
              .product(entity)
              .build();
          currentRes.add(newRes);
        } else {
          currentRes.stream()
              .filter(curr -> curr.getId().equals(in.getId()))
              .findFirst()
              .ifPresent(curr -> {
                curr.setName(in.getName());
                curr.setUrl(in.getUrl());
                curr.setIsPrimary(in.getIsPrimary());
                curr.setType(in.getType());
              });
        }
      }
    }
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
        .isNewArrival(product.getIsNewArrival())
        .rating(product.getRating())
        .categoryId(product.getCategory().getId())
        .categoryName(product.getCategory().getName())
        .categoryTypeId(product.getCategoryType().getId())
        .categoryTypeName(product.getCategoryType().getName())
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
