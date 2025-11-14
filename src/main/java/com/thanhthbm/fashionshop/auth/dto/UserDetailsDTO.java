package com.thanhthbm.fashionshop.auth.dto;

import com.thanhthbm.fashionshop.entity.Address;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsDTO {
  private UUID id;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String email;
  private Object authorityList;
  private List<Address> addressList;
}
