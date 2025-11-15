package com.thanhthbm.fashionshop.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
  private UUID categoryId;
  private UUID categoryTypeId;
  private Boolean isNewArrival;
  private Pageable pageable;
}
