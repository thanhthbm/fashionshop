package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.config.VNPayConfig;
import com.thanhthbm.fashionshop.constant.OrderStatus;
import com.thanhthbm.fashionshop.constant.PaymentStatus;
import com.thanhthbm.fashionshop.dto.Cart.CartItem;
import com.thanhthbm.fashionshop.dto.Mapper.OrderMapper;
import com.thanhthbm.fashionshop.dto.Order.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.Order.OrderDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderResponse;
import com.thanhthbm.fashionshop.dto.Order.PendingOrderDTO;
import com.thanhthbm.fashionshop.entity.Address;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.entity.OrderItem;
import com.thanhthbm.fashionshop.entity.Payment;
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.entity.Resources;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.AddressRepository;
import com.thanhthbm.fashionshop.repository.OrderRepository;
import com.thanhthbm.fashionshop.repository.ProductVariantRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
  private final RedisTemplate<String, Object> redisTemplate;
  private final CustomUserDetailService customUserDetailService;

  @Transactional(rollbackFor = Exception.class)
  public OrderResponse createOrder(User user, CheckoutRequest request) {
    // 1. Lấy giỏ hàng
    List<CartItem> cartItems = cartService.getCart(user.getId());
    if (cartItems == null || cartItems.isEmpty()) {
      throw new RuntimeException("Giỏ hàng trống.");
    }

    // 2. Validate Address
    Address address = addressRepository.findById(request.getAddressId())
        .filter(a -> a.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không hợp lệ"));

    // 3. Tính toán tổng tiền
    BigDecimal shippingFee = BigDecimal.valueOf(request.getShippingFee() != null ? request.getShippingFee() : 0);
    BigDecimal subTotal = BigDecimal.ZERO;

    for (CartItem item : cartItems) {
      // Chỉ check xem sản phẩm có tồn tại không để lấy giá, CHƯA TRỪ KHO
      ProductVariant variant = productVariantRepository.findById(item.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: " + item.getProductName()));

      // Check sơ bộ tồn kho (để báo lỗi sớm cho user đỡ mất công thanh toán)
      if (variant.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Sản phẩm " + variant.getProduct().getName() + " không đủ số lượng.");
      }

      BigDecimal currentPrice = variant.getProduct().getPrice();
      BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
      subTotal = subTotal.add(lineTotal);
    }

    BigDecimal totalAmount = subTotal.add(shippingFee);
    System.out.println("totalAmount: " + totalAmount);

    // --- CASE 1: THANH TOÁN VNPAY ---
    if ("VNPAY".equals(request.getPaymentMethod())) {
      String txnRef = VNPayConfig.getRandomNumber(8);

      // Lưu thông tin đặt hàng vào Redis (Tồn tại 15 phút)
      PendingOrderDTO pendingOrder = PendingOrderDTO.builder()
          .userId(user.getId())
          .addressId(request.getAddressId())
          .note(request.getNote())
          .shippingFee(request.getShippingFee())
          .totalAmount(totalAmount.doubleValue())
          .build();

      String redisKey = "PENDING_ORDER:" + txnRef;
      redisTemplate.opsForValue().set(redisKey, pendingOrder, 15, TimeUnit.MINUTES);

      String paymentUrl = vnPayService.getVNPayPaymentUrl(
          (int) totalAmount.doubleValue(),
          txnRef,
          vnp_Returnurl
      );

      return OrderResponse.builder()
          .paymentMethod("VNPAY")
          .paymentStatus("PENDING")
          .paymentUrl(paymentUrl)
          .totalAmount(totalAmount.doubleValue())
          .build();
    }

    // --- CASE 2: COD
    return processSaveOrderToDB(user, request, cartItems, totalAmount, address);
  }

  public List<OrderDTO> getMyOrders(User user) {
    List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);

    return orders.stream().map(orderMapper::toOrderDTO).collect(Collectors.toList());
  }

  public OrderDTO getOrderById(UUID orderId) {
    Order order = orderRepository.findById(orderId).orElse(null);
    return orderMapper.toOrderDTO(order);
  }



  @Transactional
  public OrderResponse processSaveOrderToDB(User user, CheckoutRequest request, List<CartItem> cartItems, BigDecimal totalAmount, Address address) {
    Order order = Order.builder()
        .user(user)
        .address(address)
        .orderDate(new Date())
        .paymentMethod(request.getPaymentMethod())
        .orderStatus(OrderStatus.PENDING)
        .totalAmount(totalAmount.doubleValue())
        .build();

    List<OrderItem> orderItems = new ArrayList<>();

    for (CartItem item : cartItems) {
      ProductVariant variant = productVariantRepository.findByIdWithLock(item.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: " + item.getProductName()));

      if (variant.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Sản phẩm " + variant.getProduct().getName() + " đã hết hàng trong lúc bạn thanh toán.");
      }

      // Trừ kho thật
      variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
      productVariantRepository.save(variant);

      OrderItem orderItem = OrderItem.builder()
          .order(order)
          .product(variant.getProduct())
          .productVariant(variant)
          .quantity(item.getQuantity())
          .itemPrice(variant.getProduct().getPrice().doubleValue())
          .build();

      orderItems.add(orderItem);
    }

    order.setOrderItemList(orderItems);

    Payment payment = Payment.builder()
        .order(order)
        .amount(order.getTotalAmount())
        .paymentMethod(request.getPaymentMethod())
        .paymentStatus("VNPAY".equals(request.getPaymentMethod()) ? PaymentStatus.COMPLETED : PaymentStatus.PENDING)
        .paymentDate(new Date())
        .build();

    order.setPayment(payment);

    Order savedOrder = orderRepository.save(order);

    // Xóa giỏ hàng
    cartService.clearCart(user.getId());

    return OrderResponse.builder()
        .orderId(savedOrder.getId())
        .orderCode(savedOrder.getId().toString())
        .totalAmount(savedOrder.getTotalAmount())
        .orderStatus(savedOrder.getOrderStatus())
        .build();
  }

  @Transactional
  public void processVnpayPaymentReturn(HttpServletRequest request) {
    int paymentStatus = vnPayService.orderReturn(request);

    // Lấy TxnRef chính là mã chúng ta đã gửi đi và dùng làm key Redis
    String txnRef = request.getParameter("vnp_TxnRef");
    String responseCode = request.getParameter("vnp_ResponseCode");

    if (paymentStatus == 1 && "00".equals(responseCode)) {
      // 1. Lấy thông tin đơn hàng tạm từ Redis
      String redisKey = "PENDING_ORDER:" + txnRef;
      PendingOrderDTO pendingOrder = (PendingOrderDTO) redisTemplate.opsForValue().get(redisKey);

      if (pendingOrder == null) {
        throw new RuntimeException("Giao dịch không tồn tại hoặc đã hết hạn.");
      }

      User user = customUserDetailService.findById(pendingOrder.getUserId())
          .orElseThrow(() -> new RuntimeException("User not found"));

      Address address = addressRepository.findById(pendingOrder.getAddressId())
          .orElseThrow(() -> new RuntimeException("Address not found"));

      List<CartItem> cartItems = cartService.getCart(user.getId());
      if (cartItems.isEmpty()) {
        throw new RuntimeException("Giỏ hàng đã bị thay đổi.");
      }

      CheckoutRequest checkoutRequest = new CheckoutRequest();
      checkoutRequest.setAddressId(pendingOrder.getAddressId());
      checkoutRequest.setPaymentMethod("VNPAY");
      checkoutRequest.setNote(pendingOrder.getNote());
      checkoutRequest.setShippingFee(pendingOrder.getShippingFee());

      processSaveOrderToDB(user, checkoutRequest, cartItems, BigDecimal.valueOf(pendingOrder.getTotalAmount()), address);

      redisTemplate.delete(redisKey);

    } else {
      // Thanh toán thất bại:
      // KHÔNG LÀM GÌ CẢ.
      // Vì đơn chưa tạo trong DB, Cart chưa xóa, Kho chưa trừ.
      // User quay lại trang web vẫn thấy Cart còn nguyên.
    }
  }
}