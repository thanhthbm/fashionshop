package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.OrderRequest;
import com.thanhthbm.fashionshop.dto.OrderResponse;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.entity.OrderItem;
import com.thanhthbm.fashionshop.entity.OrderStatus;
import com.thanhthbm.fashionshop.entity.Payment;
import com.thanhthbm.fashionshop.entity.PaymentStatus;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.repository.OrderRepository;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductService productService;


  @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Principal principal) throws Exception {
      User user = (User) userDetailsService.loadUserByUsername(principal.getName());
      Address address = user.getAddressList().stream().filter(address1 -> orderRequest.getAddressId().equals(address1.getId())).findFirst().orElseThrow(BadRequestException::new);

      Order order= Order.builder()
          .user(user)
          .address(address)
          .totalAmount(orderRequest.getTotalAmount())
          .orderDate(orderRequest.getOrderDate())
          .discount(orderRequest.getDiscount())
          .expectedDeliveryDate(orderRequest.getExpectedDeliveryDate())
          .paymentMethod(orderRequest.getPaymentMethod())
          .orderStatus(OrderStatus.PENDING)
          .build();
      List<OrderItem> orderItems = orderRequest.getOrderItemRequests().stream().map(orderItemRequest -> {
        try {
          Product product= productService.fetchProductById(orderItemRequest.getProductId());
          OrderItem orderItem= OrderItem.builder()
              .product(product)
              .productVariantId(orderItemRequest.getProductVariantId())
              .quantity(orderItemRequest.getQuantity())
              .order(order)
              .build();
          return orderItem;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).toList();

      order.setOrderItemList(orderItems);
      Payment payment=new Payment();
      payment.setPaymentStatus(PaymentStatus.PENDING);
      payment.setPaymentDate(new Date());
      payment.setOrder(order);
      payment.setAmount(order.getTotalAmount());
      payment.setPaymentMethod(order.getPaymentMethod());
      order.setPayment(payment);

      Order savedOrder=orderRepository.save(order);

    OrderResponse orderResponse = OrderResponse.builder()
        .paymentMethod(order.getPaymentMethod())
        .orderId(savedOrder.getId())
        .build();

    return orderResponse;
  }


}
