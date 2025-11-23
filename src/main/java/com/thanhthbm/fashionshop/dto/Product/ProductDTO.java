package com.thanhthbm.fashionshop.dto.Product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal price;
  private String brand;
  private boolean isNewArrival;
  private UUID categoryId;
  private UUID categoryTypeId;
  private String categoryName;
  private String thumbnail;
  private String categoryTypeName;
  private List<ProductVariantDTO> variants;
  private List<ProductResourceDTO> productResources;
  private Float rating;
  private String slug;
}
