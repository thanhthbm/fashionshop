package com.thanhthbm.fashionshop.dto.Order;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingOrderDTO implements Serializable {
  private UUID userId;
  private UUID addressId;
  private String note;
  private Double shippingFee;
  private Double totalAmount;
}