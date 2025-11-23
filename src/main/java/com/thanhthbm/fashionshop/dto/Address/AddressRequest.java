package com.thanhthbm.fashionshop.dto.Address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {

  @NotBlank(message = "Tên người nhận không được để trống")
  private String receiverName;

  @NotBlank(message = "Số điện thoại không được để trống")
  private String phoneNumber;

  @NotBlank(message = "Địa chỉ chi tiết không được để trống")
  private String detail;

  @NotBlank(message = "Xã/Phường không được để trống")
  private String ward;


  @NotBlank(message = "Tỉnh/Thành phố không được để trống")
  private String province;

  private Boolean isDefault;
}