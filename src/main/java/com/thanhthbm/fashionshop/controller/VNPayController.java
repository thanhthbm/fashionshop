package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.service.OrderService;
import com.thanhthbm.fashionshop.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class VNPayController {
  @Autowired
  private VNPayService vnPayService;

  @Autowired
  private OrderService orderService;


  @GetMapping("/vnpay_return")
  public ResponseEntity<?> paymentReturn(HttpServletRequest request) {
    try {
      orderService.processVnpayPaymentReturn(request);

      String responseCode = request.getParameter("vnp_ResponseCode");
      String orderInfo = request.getParameter("vnp_OrderInfo");

      Map<String, Object> result = new HashMap<>();
      result.put("orderId", orderInfo);

      if ("00".equals(responseCode)) {
        result.put("status", "success");
        result.put("message", "Payment Successful");
      } else {
        result.put("status", "failed");
        result.put("message", "Payment Failed or Cancelled. Cart Restored.");
      }

      return ResponseEntity.ok(result);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
    }
  }
}
