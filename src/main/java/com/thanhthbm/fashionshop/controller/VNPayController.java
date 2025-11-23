package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
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

  @PostMapping("/create_payment")
  public ResponseEntity<?> createPayment(@RequestParam("amount") int orderTotal,
      @RequestParam("orderInfo") String orderInfo,
      HttpServletRequest request) {
    String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

    String vnpayUrl = vnPayService.getVNPayPaymentUrl(orderTotal, orderInfo, baseUrl);

    return ResponseEntity.ok(Map.of("url", vnpayUrl));
  }

  @GetMapping("/vnpay_return")
  public ResponseEntity<?> paymentReturn(HttpServletRequest request) {
    int paymentStatus = vnPayService.orderReturn(request);

    String orderInfo = request.getParameter("vnp_OrderInfo");
    String paymentTime = request.getParameter("vnp_PayDate");
    String transactionId = request.getParameter("vnp_TransactionNo");
    String totalPrice = request.getParameter("vnp_Amount");

    Map<String, Object> result = Map.of(
        "status", paymentStatus == 1 ? "success" : "failed",
        "orderId", orderInfo,
        "totalPrice", totalPrice,
        "paymentTime", paymentTime,
        "transactionId", transactionId
    );

    return ResponseEntity.ok(result);
  }
}
