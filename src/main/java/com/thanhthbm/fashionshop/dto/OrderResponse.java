package com.thanhthbm.fashionshop.dto;

import com.thanhthbm.fashionshop.entity.Order;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

  private UUID orderId;
  private Map<String, String> credentials;
  private String paymentMethod;

}
