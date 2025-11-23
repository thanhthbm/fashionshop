package com.thanhthbm.fashionshop.dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
  private UUID id;

  private Integer quantity;
  private Double itemPrice;
  private Double subTotal;

  private UUID productVariantId;
  private String variantName;
  private UUID productId;
  private String productName;
  private String productBrand;
  private String productSlug;
  private String productThumbnail;
}