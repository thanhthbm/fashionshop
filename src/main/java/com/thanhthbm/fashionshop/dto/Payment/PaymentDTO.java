package com.thanhthbm.fashionshop.dto.Payment;


import com.thanhthbm.fashionshop.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
  private UUID id;
  private Date paymentDate;
  private String paymentMethod;
  private Double amount;
  private PaymentStatus paymentStatus;
}