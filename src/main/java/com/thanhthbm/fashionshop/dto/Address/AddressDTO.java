package com.thanhthbm.fashionshop.dto.Address;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {
  private UUID id;
  private String receiverName;
  private String phoneNumber;
  private String detail;
  private String ward;
  private String province;
}
