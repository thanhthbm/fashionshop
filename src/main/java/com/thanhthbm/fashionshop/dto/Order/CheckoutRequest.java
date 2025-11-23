package com.thanhthbm.fashionshop.dto.Order;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CheckoutRequest {
  @NotNull(message = "Address   ID is required")
  private UUID addressId;

  @NotNull(message = "Payment method is required")
  private String paymentMethod;

  private String note;

  private Double shippingFee;
}
