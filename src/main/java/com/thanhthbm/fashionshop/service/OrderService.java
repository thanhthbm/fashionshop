package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.CartItem;
import com.thanhthbm.fashionshop.dto.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.OrderResponse;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.entity.OrderItem;
import com.thanhthbm.fashionshop.constant.OrderStatus;
import com.thanhthbm.fashionshop.entity.Payment;
import com.thanhthbm.fashionshop.constant.PaymentStatus;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import com.thanhthbm.fashionshop.repository.OrderRepository;
import com.thanhthbm.fashionshop.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final UserDetailsService userDetailsService;

  private final OrderRepository orderRepository;

  private final ProductService productService;

  private final CartService cartService;

  private final AddressRepository addressRepository;

  private final ProductVariantRepository productVariantRepository;


  @Transactional(rollbackFor = Exception.class)
  public OrderResponse createOrder(User user, CheckoutRequest request) {

    List<CartItem> cartItems = cartService.getCart(user.getId());
    if (cartItems == null || cartItems.isEmpty()) {
      throw new RuntimeException("Giỏ hàng trống, vui lòng thêm sản phẩm trước khi thanh toán.");
    }

    Address address = addressRepository.findById(request.getAddressId())
        .filter(a -> a.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không hợp lệ hoặc không tồn tại"));

    Order order = Order.builder()
        .user(user)
        .address(address)
        .orderDate(new Date())
        .paymentMethod(request.getPaymentMethod())
        .orderStatus(OrderStatus.PENDING)
        .build();

    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (CartItem item : cartItems) {
      ProductVariant variant = productVariantRepository.findByIdWithLock(item.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm (Variant) không tồn tại: " + item.getProductName()));

      if (variant.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Sản phẩm " + variant.getProduct().getName()
            + " (" + variant.getColor() + " - " + variant.getSize() + ") không đủ số lượng trong kho.");
      }

      variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
      productVariantRepository.save(variant);

      BigDecimal currentPrice = variant.getProduct().getPrice();

      BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
      totalAmount = totalAmount.add(lineTotal);

      OrderItem orderItem = OrderItem.builder()
          .order(order)
          .product(variant.getProduct())
          .productVariantId(variant.getId())
          .quantity(item.getQuantity())
          .itemPrice(currentPrice.doubleValue())
          .build();

      orderItems.add(orderItem);
    }

    order.setOrderItemList(orderItems);
    order.setTotalAmount(totalAmount.doubleValue());

    Payment payment = Payment.builder()
        .order(order)
        .amount(order.getTotalAmount())
        .paymentMethod(request.getPaymentMethod())
        .paymentStatus(PaymentStatus.PENDING)
        .paymentDate(new Date())
        .build();
    order.setPayment(payment);

    Order savedOrder = orderRepository.save(order);

    cartService.clearCart(user.getId());

    return OrderResponse.builder()
        .orderId(savedOrder.getId())
        .paymentMethod(savedOrder.getPaymentMethod())
        .build();
  }


}
