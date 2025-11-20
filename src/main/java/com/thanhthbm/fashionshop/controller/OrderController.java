package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.dto.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.OrderResponse;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.service.OrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
