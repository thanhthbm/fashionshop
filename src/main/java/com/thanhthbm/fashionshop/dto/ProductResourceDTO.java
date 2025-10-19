package com.thanhthbm.fashionshop.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResourceDTO {
  private UUID id;
  private String name;
  private String url;
  private String type;
  private Boolean isPrimary;
}
