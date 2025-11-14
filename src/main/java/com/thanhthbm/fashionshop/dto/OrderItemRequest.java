package com.thanhthbm.fashionshop.dto;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
  private UUID productId;
  private UUID productVariantId;
  private Integer quantity;
  private Double discount;
}
