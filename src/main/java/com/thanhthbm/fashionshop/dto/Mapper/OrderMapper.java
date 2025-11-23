package com.thanhthbm.fashionshop.dto.Mapper;

import com.thanhthbm.fashionshop.dto.Address.AddressDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderItemDTO;
import com.thanhthbm.fashionshop.dto.Payment.PaymentDTO;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.entity.OrderItem;
import com.thanhthbm.fashionshop.entity.Payment;
import com.thanhthbm.fashionshop.entity.Product;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public OrderDTO toOrderDTO(Order order) {
    if (order == null) {
      return null;
    }

    return OrderDTO.builder()
        .id(order.getId())
        .orderDate(order.getOrderDate())
        .orderStatus(order.getOrderStatus())
        .totalAmount(order.getTotalAmount())
        .discount(order.getDiscount())
        .paymentMethod(order.getPaymentMethod())
        .shipmentTrackingNumber(order.getShipmentTrackingNumber())
        .expectedDeliveryDate(order.getExpectedDeliveryDate())
        .shippingAddress(toAddressDTO(order.getAddress()))
        .paymentInfo(toPaymentDTO(order.getPayment()))
        .orderItems(order.getOrderItemList() != null
            ? order.getOrderItemList().stream()
            .map(this::toOrderItemDTO)
            .collect(Collectors.toList())
            : Collections.emptyList())
        .build();
  }

  public OrderItemDTO toOrderItemDTO(OrderItem item) {
    if (item == null) {
      return null;
    }

    Product product = item.getProduct();
    String thumbnailUrl = null;
    String productName = "Unknown Product";
    String productBrand = "";
    String productSlug = "";
    java.util.UUID productId = null;

    if (product != null) {
      productId = product.getId();
      productName = product.getName();
      productBrand = product.getBrand();
      productSlug = product.getSlug();

      if (product.getResources() != null && !product.getResources().isEmpty()) {
        thumbnailUrl = product.getResources().get(0).getUrl();
      }
    }

    return OrderItemDTO.builder()
        .id(item.getId())
        .quantity(item.getQuantity())
        .itemPrice(item.getItemPrice())
        .subTotal(item.getQuantity() * item.getItemPrice())
        .productVariantId(item.getProductVariant().getId())
        .variantName(item.getProductVariant().getColor() + "-" + item.getProductVariant().getSize())
        // Các field từ Product
        .productId(productId)
        .productName(productName)
        .productBrand(productBrand)
        .productSlug(productSlug)
        .productThumbnail(thumbnailUrl)
        .build();
  }

  public AddressDTO toAddressDTO(Address address) {
    if (address == null) {
      return null;
    }
    return AddressDTO.builder()
        .id(address.getId())
        .receiverName(address.getReceiverName())
        .phoneNumber(address.getPhoneNumber())
        .detail(address.getDetail())
        .ward(address.getWard())
        .province(address.getProvince())
        .build();
  }

  public PaymentDTO toPaymentDTO(Payment payment) {
    if (payment == null) {
      return null;
    }
    return PaymentDTO.builder()
        .id(payment.getId())
        .paymentDate(payment.getPaymentDate())
        .paymentMethod(payment.getPaymentMethod())
        .amount(payment.getAmount())
        .paymentStatus(payment.getPaymentStatus())
        .build();
  }
}
