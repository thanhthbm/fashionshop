package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.dto.Order.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.Order.OrderDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderResponse;
import com.thanhthbm.fashionshop.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid CheckoutRequest checkoutRequest
  ) {

    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    OrderResponse orderResponse = orderService.createOrder(user, checkoutRequest);
    return ResponseEntity.ok(ApiResponse.success(orderResponse));
  }


  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(user)));
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable("orderId") UUID orderId) {
    return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(orderId)));
  }
}
