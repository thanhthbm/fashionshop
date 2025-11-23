package com.thanhthbm.fashionshop.dto.Product;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTypeDTO {
  private UUID id;
  private String name;
  private String code;
  private String description;
}
