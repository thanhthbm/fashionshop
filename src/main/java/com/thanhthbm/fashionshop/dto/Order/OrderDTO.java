package com.thanhthbm.fashionshop.dto.Order;


import com.thanhthbm.fashionshop.constant.OrderStatus;
import com.thanhthbm.fashionshop.dto.Address.AddressDTO;
import com.thanhthbm.fashionshop.dto.Payment.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

  private UUID id;

  private Date orderDate;
  private OrderStatus orderStatus;
  private Double totalAmount;
  private Double discount;

  private String paymentMethod;
  private String shipmentTrackingNumber;
  private Date expectedDeliveryDate;

  private AddressDTO shippingAddress;
  private PaymentDTO paymentInfo;
  private List<OrderItemDTO> orderItems;

}
