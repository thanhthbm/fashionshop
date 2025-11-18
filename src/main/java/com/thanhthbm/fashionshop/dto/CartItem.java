package com.thanhthbm.fashionshop.dto;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem implements Serializable {
  private UUID productId;
  private UUID variantId;
  private String productName;
  private Double price;
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;
  private String imageUrl;
}
