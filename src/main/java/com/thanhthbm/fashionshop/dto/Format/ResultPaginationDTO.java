package com.thanhthbm.fashionshop.dto.Format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultPaginationDTO {
  private Meta meta;
  private Object result;

  @Getter
  @Setter
  @Builder
  public static class Meta {
    private int page;
    private int pageSize;
    private long total;
    private int pages;
  }
}
