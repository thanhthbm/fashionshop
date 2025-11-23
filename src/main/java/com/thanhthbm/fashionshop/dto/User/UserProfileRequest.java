package com.thanhthbm.fashionshop.dto.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileRequest {
  @NotNull(message = "First name must not be blank")
  private String firstName;
  @NotNull(message = "Last name must not be blank")
  private String lastName;
  private String phoneNumber;
}
