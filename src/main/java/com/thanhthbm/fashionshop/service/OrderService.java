package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.constant.OrderStatus;
import com.thanhthbm.fashionshop.constant.PaymentStatus;
import com.thanhthbm.fashionshop.dto.Cart.CartItem;
import com.thanhthbm.fashionshop.dto.Mapper.OrderMapper;
import com.thanhthbm.fashionshop.dto.Order.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.Order.OrderDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderResponse;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.entity.OrderItem;
import com.thanhthbm.fashionshop.entity.Payment;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import com.thanhthbm.fashionshop.repository.OrderRepository;
import com.thanhthbm.fashionshop.repository.ProductVariantRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final CartService cartService;
  private final AddressRepository addressRepository;
  private final ProductVariantRepository productVariantRepository;
  private final OrderMapper orderMapper;
  private final VNPayService vnPayService;
  public static String vnp_Returnurl = "http://localhost:3000";

  @Transactional(rollbackFor = Exception.class)
  public OrderResponse createOrder(User user, CheckoutRequest request) {
    List<CartItem> cartItems = cartService.getCart(user.getId());
    if (cartItems == null || cartItems.isEmpty()) {
      throw new RuntimeException("Giỏ hàng trống, vui lòng thêm sản phẩm trước khi thanh toán.");
    }

    Address address = addressRepository.findById(request.getAddressId())
        .filter(a -> a.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không hợp lệ hoặc không tồn tại"));

    BigDecimal shippingFee = BigDecimal.valueOf(request.getShippingFee() != null ? request.getShippingFee() : 0);

    Order order = Order.builder()
        .user(user)
        .address(address)
        .orderDate(new Date())
        .paymentMethod(request.getPaymentMethod())
        .orderStatus(OrderStatus.PENDING)
        .shipmentTrackingNumber(null)
        .build();

    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal subTotal = BigDecimal.ZERO;

    for (CartItem item : cartItems) {
      ProductVariant variant = productVariantRepository.findByIdWithLock(item.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa: " + item.getProductName()));

      if (variant.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Sản phẩm " + variant.getProduct().getName()
            + " (" + variant.getColor() + " - " + variant.getSize() + ") không đủ số lượng trong kho.");
      }

      variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
      productVariantRepository.save(variant);

      BigDecimal currentPrice = variant.getProduct().getPrice();
      BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
      subTotal = subTotal.add(lineTotal);

      OrderItem orderItem = OrderItem.builder()
          .order(order)
          .product(variant.getProduct())
          .productVariant(variant)
          .quantity(item.getQuantity())
          .itemPrice(currentPrice.doubleValue())
          .build();

      orderItems.add(orderItem);
    }

    BigDecimal totalAmount = subTotal.add(shippingFee);

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


    String paymentUrl = null;

    // TODO: Nếu sau này tích hợp VNPAY, check ở đây:
     if ("VNPAY".equals(request.getPaymentMethod())) {
       long vnpayAmount = BigDecimal.valueOf(savedOrder.getTotalAmount())
           .multiply(BigDecimal.valueOf(100))
           .longValue();

       paymentUrl = vnPayService.getVNPayPaymentUrl(
           (int) vnpayAmount,
           "Thanh toan hoa don " + savedOrder.getId(),
           vnp_Returnurl
           );
     }

    return OrderResponse.builder()
        .orderId(savedOrder.getId())
        .orderCode(savedOrder.getId().toString())
        .totalAmount(savedOrder.getTotalAmount())
        .paymentMethod(savedOrder.getPaymentMethod())
        .paymentStatus(savedOrder.getPayment().getPaymentStatus().name())
        .orderStatus(savedOrder.getOrderStatus())
        .orderDate(savedOrder.getOrderDate())
        .paymentUrl(paymentUrl)
        .build();
  }

  public List<OrderDTO> getMyOrders(User user) {
    List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);

    return orders.stream().map(orderMapper::toOrderDTO).collect(Collectors.toList());
  }

  public OrderDTO getOrderById(UUID orderId) {
    Order order = orderRepository.findById(orderId).orElse(null);
    return orderMapper.toOrderDTO(order);
  }
}