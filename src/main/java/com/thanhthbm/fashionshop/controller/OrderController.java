package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.dto.ApiResponse;
import com.thanhthbm.fashionshop.dto.OrderRequest;
import com.thanhthbm.fashionshop.dto.OrderResponse;
import com.thanhthbm.fashionshop.entity.Order;
import com.thanhthbm.fashionshop.service.OrderService;
import java.security.Principal;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest orderRequest, Principal principal)
      throws Exception {
    OrderResponse orderResponse = orderService.createOrder(orderRequest, principal);

    return new ResponseEntity<>(ApiResponse.success(orderResponse), HttpStatus.OK);
  }
}
