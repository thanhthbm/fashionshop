package com.thanhthbm.fashionshop.dto.Order;

import com.thanhthbm.fashionshop.constant.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Date;
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
  private String paymentMethod;
  private String paymentStatus;
  private String paymentUrl;
  private String orderCode;
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
  private Date orderDate;
  private Double totalAmount;

}
