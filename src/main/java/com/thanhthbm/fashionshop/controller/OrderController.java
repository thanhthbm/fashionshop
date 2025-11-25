package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.dto.Format.ResultPaginationDTO;
import com.thanhthbm.fashionshop.dto.Order.CheckoutRequest;
import com.thanhthbm.fashionshop.dto.Order.OrderDTO;
import com.thanhthbm.fashionshop.dto.Order.OrderResponse;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.service.OrderService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllOrdersAdmin(
      @Filter Specification<Order> spec,
      Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrdersAdmin(spec, pageable)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(
      @PathVariable UUID id,
      @RequestBody OrderDTO orderDTO
  ) {
    return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderAdmin(id, orderDTO)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(user)));
  }

  @GetMapping("/{orderId:[0-9a-fA-F-]{36}}")
  public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable("orderId") UUID orderId) {
    return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(orderId)));
  }
}
